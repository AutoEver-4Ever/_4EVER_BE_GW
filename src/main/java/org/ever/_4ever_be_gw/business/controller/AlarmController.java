package org.ever._4ever_be_gw.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.ever._4ever_be_gw.business.dto.alarm.request.NotificationMarkReadRequestDto;
import org.ever._4ever_be_gw.business.dto.alarm.response.NotificationCountResponseDto;
import org.ever._4ever_be_gw.business.dto.alarm.response.NotificationListResponseDto;
import org.ever._4ever_be_gw.business.dto.alarm.response.NotificationReadResponseDto;
import org.ever._4ever_be_gw.common.dto.PageDto;
import org.ever._4ever_be_gw.common.dto.PageResponseDto;
import org.ever._4ever_be_gw.common.dto.ValidUuidV7;
import org.ever._4ever_be_gw.common.response.ApiResponse;
import org.ever._4ever_be_gw.common.util.UuidV7;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/alarm/notifications")
@Validated
@Tag(name = "알림", description = "알림(Notification) API")
public class AlarmController {

    final static int MAX_PAGE_SIZE = 100;
    private static final List<String> SOURCES =
        List.of("PR", "SD", "IM", "FCM", "HRM", "PP", "CUS", "SUP");
    private static final List<String> LINK_TYPES = List.of(
        "PURCHASE_REQUISITION", "PURCHASE_ORDER", "PR_ETC",
        "QUOTATION", "SALES_ORDER", "SD_ETC",
        "IM_ETC",
        "SALES_INVOICE", "PURCHASE_INVOICE", "FCM_ETC",
        "HRM_ETC",
        "ESTIMATE", "INSUFFICIENT_STOCK", "PP_ETC");

    // ===== 알림 목록 조회 =====
    @GetMapping("/list")
    @Operation(
        summary = "알림 목록 조회",
        description = "알림 목록을 페이징/정렬/필터와 함께 조회합니다."
    )
    public ResponseEntity<ApiResponse<PageResponseDto<NotificationListResponseDto>>> getNotificationList(
        @RequestParam(name = "sortBy", required = false, defaultValue = "createdAt")
        String sortBy,
        @RequestParam(name = "order", required = false, defaultValue = "desc")
        String order,
        @RequestParam(name = "source", required = false)
        String source,
        @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다.")
        @RequestParam(name = "page", required = false, defaultValue = "0")
        Integer page,
        @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
        @Max(value = MAX_PAGE_SIZE, message = "페이지 크기는 최대 " + MAX_PAGE_SIZE + "까지 가능합니다.")
        @RequestParam(name = "size", required = false, defaultValue = "20")
        Integer size
    ) {

        // sortBy 화이트리스트 (지원하지 않는 값은 기본값으로)
        final List<String> ALLOWED_SORTS = List.of("createdAt");
        if (!ALLOWED_SORTS.contains(sortBy)) {
            sortBy = "createdAt";
        }

        // order 화이트리스트 (지원하지 않는 값은 기본값으로)
        final List<String> ALLOWED_ORDERS = List.of("asc", "desc");
        if (!ALLOWED_ORDERS.contains(order.toLowerCase())) {
            order = "desc";
        }

        // 2. source 필터 → 예외 발생 (개선)
        if (source != null && !source.isBlank()) {
            source = source.trim().toUpperCase();
            if (!SOURCES.contains(source)) {
                throw new IllegalArgumentException(
                    "유효하지 않은 source 값입니다. 허용값: " + String.join(", ", SOURCES)
                );
            }
        } else {
            source = null; // 빈값은 null로 통일
        }

        // TODO ALARM 서버와 연동하여 실제 데이터 조회
        List<NotificationListResponseDto> all = generateMockNotifications(60);

        // source 필터: 허용된 값만 적용
        if (source != null && !source.isBlank()) {
            String src = source.trim();
            boolean validSource = SOURCES.stream().anyMatch(sv -> sv.equalsIgnoreCase(src));
            if (validSource) {
                all = all.stream()
                    .filter(n -> src.equalsIgnoreCase(String.valueOf(n.getSource())))
                    .collect(Collectors.toList());
            } else {
                source = null; // 유효하지 않은 값은 null로 통일
            }
        } else {
            source = null; // 빈값은 null로 통일
        }

        // 정렬 (createdAt만 지원) - null-safe comparator
        if ("createdAt".equalsIgnoreCase(sortBy)) {
            Comparator<NotificationListResponseDto> cmp = Comparator.comparing(
                NotificationListResponseDto::getCreatedAt,
                Comparator.nullsLast(Comparator.naturalOrder()));
            if ("desc".equalsIgnoreCase(order)) {
                cmp = cmp.reversed();
            }
            all = all.stream().sorted(cmp).collect(Collectors.toList());
        }

        int total = all.size();
        int fromIdx = Math.min(page * size, total);
        int toIdx = Math.min(fromIdx + size, total);
        List<NotificationListResponseDto> pageItems;
        if (fromIdx >= toIdx) {
            pageItems = List.of();
        } else {
            pageItems = all.subList(fromIdx, toIdx);
        }

        int totalPages = total == 0 ? 0 : (int) Math.ceil((double) total / size);
        boolean hasNext = page + 1 < totalPages;
        PageDto pageDto = PageDto.builder().number(page).size(size).totalElements(total)
            .totalPages(totalPages).hasNext(hasNext).build();

        PageResponseDto<NotificationListResponseDto> payload = PageResponseDto.<NotificationListResponseDto>builder()
            .items(pageItems)
            .page(pageDto)
            .build();

        return ResponseEntity.ok(
            ApiResponse.success(payload, "알림 목록을 성공적으로 조회했습니다.", HttpStatus.OK));
    }

    // ===== 알림 갯수 조회 =====
    @GetMapping("/count")
    @Operation(summary = "알림 갯수 조회", description = "상태별(READ/UNREAD) 알림 갯수를 조회합니다.")
    public ResponseEntity<ApiResponse<NotificationCountResponseDto>> getNotificationCount(
        @RequestParam(name = "status", required = false) String status
    ) {
        // 단순 목업: status가 UNREAD면 3, READ면 12, 미지정이면 총 15로 가정
        final List<String> ALLOWED_STATUS = List.of("READ", "UNREAD");
        if (status != null && !status.isBlank()) {
            status = status.trim().toUpperCase();
            if (!ALLOWED_STATUS.contains(status)) {
                throw new IllegalArgumentException("유효하지 않은 status 값입니다. 허용값: READ, UNREAD");
            }
        } else {
            status = null; // 빈값은 null로 통일
        }

        // TODO ALARM 서버와 연동하여 실제 데이터 조회

        int count;
        if ("UNREAD".equalsIgnoreCase(status)) {
            count = 3;
        } else if ("READ".equalsIgnoreCase(status)) {
            count = 12;
        } else {
            count = 15;
        }

        NotificationCountResponseDto responseDto = NotificationCountResponseDto.builder()
            .count(count)
            .build();
        String msg = (status == null || status.isBlank()) ? "전체 알림 갯수를 성공적으로 조회했습니다."
            : ("UNREAD".equalsIgnoreCase(status) ? "안 읽은 알림 갯수를 성공적으로 조회했습니다."
                : "읽은 알림 갯수를 성공적으로 조회했습니다.");
        return ResponseEntity.ok(ApiResponse.success(responseDto, msg, HttpStatus.OK));
    }

    // ===== 알림 구독 요청 =====
    @PostMapping("/subscribe/{userId}")
    @Operation(summary = "알림 구독 요청", description = "사용자 구독을 등록합니다. (목업: data 없음)")
    public ResponseEntity<ApiResponse<Map<String, Object>>> subscribe(
        @PathVariable("userId") String userId
    ) {

        // TODO Service단에서 ConcurrentHashMap 등을 이용해 실제 구독 관리 필요 (목업 구현)

        Map<String, Object> data = Map.of("userId", userId, "subscribed", true);
        return ResponseEntity.ok(ApiResponse.success(data, "알림 구독이 성공적으로 등록되었습니다.", HttpStatus.OK));
    }

    // ===== 알림 읽음 처리 (목록) =====
    @PatchMapping("/list/read")
    @Operation(summary = "알림 읽음 처리(목록)", description = "주어진 알림 ID 목록을 읽음 처리합니다. (목업)")
    public ResponseEntity<ApiResponse<NotificationReadResponseDto>> markReadList(
        @Valid @RequestBody NotificationMarkReadRequestDto notificationMarkReadRequestDto
    ) {

        List<String> ids = notificationMarkReadRequestDto.getNotificationId();

        // TODO ALARM 서버와 연동하여 실제 읽음 처리

        NotificationReadResponseDto responseDto = NotificationReadResponseDto.builder()
            .processedCount(ids.size())
            .build();

        String msg =
            ids.isEmpty() ? "읽음 처리할 알림이 없습니다." : (long) ids.size() + "개의 알림을 성공적으로 읽음 처리했습니다.";

        return ResponseEntity.ok(ApiResponse.success(responseDto, msg, HttpStatus.OK));
    }

    // ===== 알림 읽음 처리 (전체) =====
    @PatchMapping("/all/read")
    @Operation(summary = "알림 읽음 처리(전체)", description = "모든 알림을 읽음 처리합니다. (목업)")
    public ResponseEntity<ApiResponse<NotificationReadResponseDto>> markReadAll() {

        NotificationReadResponseDto responseDto = NotificationReadResponseDto.builder()
            .processedCount(15)
            .build();

        return ResponseEntity.ok(
            ApiResponse.success(responseDto, "모든 알림을 성공적으로 읽음 처리했습니다.", HttpStatus.OK)
        );
    }

    // ===== 알림 읽음 처리 (단일) =====
    @PatchMapping("/{notificationId}/read")
    @Operation(summary = "알림 읽음 처리(단일)", description = "특정 알림을 읽음 처리합니다. (목업)")
    public ResponseEntity<ApiResponse<Void>> markReadOne(
        @PathVariable("notificationId")
        @ValidUuidV7
        String notificationId
    ) {
        Map<String, Object> data = Map.of("notificationId", notificationId, "status", "READ");

        // TODO ALARM 서버와 연동하여 실제 읽음 처리

        return ResponseEntity.ok(
            ApiResponse.success(null, notificationId + "알림을 성공적으로 읽음 처리했습니다.", HttpStatus.OK)
        );
    }

    private List<NotificationListResponseDto> generateMockNotifications(int size) {
        List<NotificationListResponseDto> list = new ArrayList<>();

        LocalDateTime base = LocalDateTime.now();

        for (int i = 0; i < size; i++) {
            String source = SOURCES.get(i % SOURCES.size());
            String linkType = LINK_TYPES.get(i % LINK_TYPES.size());
            NotificationListResponseDto m = NotificationListResponseDto.builder()
                .notificationId(UuidV7.randomUuidV7())
                .notificationTitle(
                    source.equals("SD") ? "판매 오더 SO-2024-%03d가 생성되었습니다".formatted(100 + i)
                        : "생산 오더 PO-2024-%03d이 승인되었습니다".formatted(100 + i))
                .notificationMessage(
                    source.equals("SD") ? "판매 오더 SO-2024-%03d가 생성되었습니다".formatted(100 + i)
                        : "생산 오더 PO-2024-%03d이 승인되었습니다".formatted(100 + i))
                .linkType(linkType)
                .linkId(UuidV7.randomUuidV7())
                .source(source)
                .createdAt(base.plusMinutes(i * 3L))
                .build();
            list.add(m);
        }

        return list;
    }
}


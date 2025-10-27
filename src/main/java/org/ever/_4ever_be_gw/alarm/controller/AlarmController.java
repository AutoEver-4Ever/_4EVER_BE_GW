package org.ever._4ever_be_gw.alarm.controller;

import com.github.f4b6a3.uuid.UuidCreator;
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
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.alarm.dto.request.AlarmServerRequestDto;
import org.ever._4ever_be_gw.alarm.dto.request.NotificationMarkReadRequestDto;
import org.ever._4ever_be_gw.alarm.dto.response.NotificationCountResponseDto;
import org.ever._4ever_be_gw.alarm.dto.response.NotificationListResponseDto;
import org.ever._4ever_be_gw.alarm.dto.response.NotificationReadResponseDto;
import org.ever._4ever_be_gw.alarm.service.AlarmHttpService;
import org.ever._4ever_be_gw.alarm.util.AlarmDtoConverter;
import org.ever._4ever_be_gw.common.dto.PageDto;
import org.ever._4ever_be_gw.common.dto.pagable.PageResponseDto;
import org.ever._4ever_be_gw.common.dto.validation.AllowedValues;
import org.ever._4ever_be_gw.common.dto.validation.ValidUuidV7;
import org.ever._4ever_be_gw.common.response.ApiResponse;
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
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/alarm/notifications")
@Validated // RequestParam 등의 유효성 검사용
@RequiredArgsConstructor
@Slf4j
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

    private final AlarmHttpService alarmHttpService;
    private final UUID tempUuid = UuidCreator.getTimeOrderedEpoch(); // 임시 사용자 UUID // TODO : 인증 연동 후 수정

    // ===== 알림 목록 조회 =====
    @GetMapping("/list")
    @Operation(
        summary = "알림 목록 조회",
        description = "알림 목록을 페이징/정렬/필터와 함께 조회합니다."
    )
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<NotificationListResponseDto>>>> getNotificationList(
        @AllowedValues(
            allowedValues = {"createdAt"},
            ignoreCase = true,
            message = "sortBy는 createdAt만 허용됩니다."
        )
        @RequestParam(name = "sortBy", required = false, defaultValue = "createdAt")
        String sortBy,
        @AllowedValues(
            allowedValues = {"asc", "desc"},
            ignoreCase = true,
            message = "order는 asc 또는 desc만 허용됩니다."
        )
        @RequestParam(name = "order", required = false, defaultValue = "desc")
        String order,
        @AllowedValues(
            allowedValues = {"PR", "SD", "IM", "FCM", "HRM", "PP", "CUS", "SUP"},
            ignoreCase = true,
            message = "유효하지 않은 source 값입니다."
        )
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

        // 외부 서버로 요청 전송
        AlarmServerRequestDto.NotificationListRequest request = AlarmDtoConverter.toServerRequest(
            tempUuid,
            sortBy,
            order,
            source,
            page,
            size
        );

        return alarmHttpService.getNotificationList(request)
            .map(serverResponse -> {
                PageResponseDto<NotificationListResponseDto> clientResponse =
                    AlarmDtoConverter.toClientResponse(serverResponse);
                return ResponseEntity.ok(
                    ApiResponse.success(clientResponse, "알림 목록을 성공적으로 조회했습니다.", HttpStatus.OK)
                );
            })
            .onErrorResume(error -> {
                // TODO 에러 코드에 따른 세분화된 처리 필요

                if (error instanceof WebClientResponseException clientError) {
                    if (clientError.getStatusCode().is4xxClientError()) {
                        log.error(
                            "[AlarmController] 알림 목록 조회 중 클라이언트 오류 발생: {}",
                            clientError.getMessage()
                        );

                        ApiResponse<PageResponseDto<NotificationListResponseDto>> apiResponse =
                            ApiResponse.fail(
                                "잘못된 요청입니다. 요청 파라미터를 확인해주세요.",
                                HttpStatus.valueOf(clientError.getStatusCode().value()),
                                null
                            );

                        return Mono.just(
                            ResponseEntity
                                .status(clientError.getStatusCode())
                                .body(apiResponse)
                        );
                    }
                }

                log.error("[AlarmController] 알림 목록 조회 중 서버 오류 발생: {}", error.getMessage());

                // 외부 서버 오류 시 목업 데이터 반환 (fallback)
                PageResponseDto<NotificationListResponseDto> fallbackResponse =
                    createFallbackNotificationList(sortBy, order, source, page,
                        size);
                return Mono.just(ResponseEntity.ok(
                    ApiResponse.success(fallbackResponse, "알림 목록을 성공적으로 조회했습니다. (목업 데이터)",
                        HttpStatus.OK)
                ));
            });
    }

    // ===== 알림 갯수 조회 =====
    @GetMapping("/count")
    @Operation(summary = "알림 갯수 조회", description = "상태별(READ/UNREAD) 알림 갯수를 조회합니다.")
//    public Mono<ResponseEntity<ApiResponse<NotificationCountResponseDto>>> getNotificationCount(
    public Mono<ResponseEntity<String>> getNotificationCount(
        @AllowedValues(
            allowedValues = {"READ", "UNREAD"},
            ignoreCase = true,
            message = "유효하지 않은 status 값입니다. 허용값: READ, UNREAD"
        )
        @RequestParam(name = "status", required = false, defaultValue = "UNREAD")
        String status
    ) {
        // 외부 서버로 요청 전송
        AlarmServerRequestDto.NotificationCountRequest request = AlarmDtoConverter.toCountServerRequest(
            tempUuid,
            status
        );

        return alarmHttpService.getNotificationCount(request)
            .map(serverResponse -> {
                NotificationCountResponseDto clientResponse = AlarmDtoConverter
                    .toClientResponse(serverResponse);
                String msg = (status == null || status.isBlank())
                    ? "전체 알림 갯수를 성공적으로 조회했습니다."
                    : ("UNREAD".equalsIgnoreCase(status)
                        ? "안 읽은 알림 갯수를 성공적으로 조회했습니다."
                        : "읽은 알림 갯수를 성공적으로 조회했습니다.");
                ApiResponse.success(clientResponse, msg, HttpStatus.OK);
                log.info("[AlarmController] 알림 갯수 조회 성공: status={}, response={}",
                    status, serverResponse);
                // TODO 삭제됨 다시 작성
                return ResponseEntity.ok("성공");
            })
            .doOnSuccess(response -> {
                log.info("[AlarmController] 알림 갯수 조회 성공: status={}, response={}",
                    status, response.getBody());
            })
            .onErrorResume(error -> {
                // 외부 서버 오류 시 목업 데이터 반환 (fallback)
                NotificationCountResponseDto fallbackResponse =
                    createFallbackNotificationCount(status);
                String msg = (status == null || status.isBlank())
                    ? "전체 알림 갯수를 성공적으로 조회했습니다. (목업 데이터)"
                    : ("UNREAD".equalsIgnoreCase(status)
                        ? "안 읽은 알림 갯수를 성공적으로 조회했습니다. (목업 데이터)"
                        : "읽은 알림 갯수를 성공적으로 조회했습니다. (목업 데이터)");
//                return Mono.just(
//                    ResponseEntity.ok(ApiResponse.success(fallbackResponse, msg, HttpStatus.OK)));
                return Mono.just(ResponseEntity.ok("성공 (목업 데이터)"));
            });
    }

    // ===== 알림 구독 요청 =====
    @PostMapping("/subscribe/{userId}")
    @Operation(summary = "알림 구독 요청", description = "사용자 구독을 등록합니다. (목업: data 없음)")
    public ResponseEntity<ApiResponse<Map<String, Object>>> subscribe(
        @ValidUuidV7
        @PathVariable("userId")
        String userId
    ) {
        // 구독 요청은 외부 서버로 전송하지 않고 로컬에서 처리
        Map<String, Object> data = Map.of("userId", userId, "subscribed", true);
        return ResponseEntity.ok(ApiResponse.success(data, "알림 구독이 성공적으로 등록되었습니다.", HttpStatus.OK));
    }

    // ===== 알림 읽음 처리 (목록) =====
    @PatchMapping("/list/read")
    @Operation(summary = "알림 읽음 처리(목록)", description = "주어진 알림 ID 목록을 읽음 처리합니다.")
    public Mono<ResponseEntity<ApiResponse<NotificationReadResponseDto>>> markReadList(
        @Valid
        @RequestBody
        NotificationMarkReadRequestDto notificationMarkReadRequestDto
    ) {

        List<String> ids = notificationMarkReadRequestDto.getNotificationId();

        // 외부 서버로 요청 전송
        AlarmServerRequestDto.NotificationMarkReadRequest request = AlarmDtoConverter.toMarkReadServerRequest(
            tempUuid,
            ids
        );

        return alarmHttpService.markReadList(request)
            .map(serverResponse -> {
                NotificationReadResponseDto clientResponse = AlarmDtoConverter.toClientResponse(
                    serverResponse);
                String msg = ids.isEmpty() ? "읽음 처리할 알림이 없습니다." :
                    (long) ids.size() + "개의 알림을 성공적으로 읽음 처리했습니다.";
                return ResponseEntity.ok(ApiResponse.success(clientResponse, msg, HttpStatus.OK));
            })
            .onErrorResume(error -> {
                // 외부 서버 오류 시 목업 응답 반환 (fallback)
                NotificationReadResponseDto fallbackResponse = NotificationReadResponseDto.builder()
                    .processedCount(ids.size())
                    .build();
                String msg = ids.isEmpty() ? "읽음 처리할 알림이 없습니다. (목업 데이터)" :
                    (long) ids.size() + "개의 알림을 성공적으로 읽음 처리했습니다. (목업 데이터)";
                return Mono.just(
                    ResponseEntity.ok(ApiResponse.success(fallbackResponse, msg, HttpStatus.OK)));
            });
    }

    // ===== 알림 읽음 처리 (전체) =====
    @PatchMapping("/all/read")
    @Operation(summary = "알림 읽음 처리(전체)", description = "모든 알림을 읽음 처리합니다.")
    public Mono<ResponseEntity<ApiResponse<NotificationReadResponseDto>>> markReadAll() {

        return alarmHttpService.markReadAll()
            .map(serverResponse -> {
                NotificationReadResponseDto clientResponse = AlarmDtoConverter.toClientResponse(
                    serverResponse);
                return ResponseEntity.ok(
                    ApiResponse.success(clientResponse, "모든 알림을 성공적으로 읽음 처리했습니다.", HttpStatus.OK)
                );
            })
            .onErrorResume(error -> {
                // 외부 서버 오류 시 목업 응답 반환 (fallback)
                NotificationReadResponseDto fallbackResponse = NotificationReadResponseDto.builder()
                    .processedCount(15)
                    .build();
                return Mono.just(ResponseEntity.ok(
                    ApiResponse.success(fallbackResponse, "모든 알림을 성공적으로 읽음 처리했습니다. (목업 데이터)",
                        HttpStatus.OK)
                ));
            });
    }

    // ===== 알림 읽음 처리 (단일) =====
    @PatchMapping("/{notificationId}/read")
    @Operation(summary = "알림 읽음 처리(단일)", description = "특정 알림을 읽음 처리합니다.")
    public Mono<ResponseEntity<ApiResponse<Void>>> markReadOne(
        @PathVariable("notificationId")
        @ValidUuidV7
        String notificationId
    ) {
        // 외부 서버로 요청 전송
        AlarmServerRequestDto.NotificationMarkReadOneRequest request = AlarmDtoConverter.toMarkReadOneServerRequest(
            tempUuid,
            notificationId
        );

        return alarmHttpService.markReadOne(request)
            .map(serverResponse -> {
                return ResponseEntity.ok(
                    ApiResponse.success((Void) null, notificationId + " 알림을 성공적으로 읽음 처리했습니다.",
                        HttpStatus.OK)
                );
            })
            .onErrorResume(error -> {
                // 외부 서버 오류 시 목업 응답 반환 (fallback)
                return Mono.just(ResponseEntity.ok(
                    ApiResponse.success(null,
                        notificationId + " 알림을 성공적으로 읽음 처리했습니다. (목업 데이터)", HttpStatus.OK)
                ));
            });
    }

    // ===== Fallback 메서드들 (외부 서버 오류 시 사용) =====

    private PageResponseDto<NotificationListResponseDto> createFallbackNotificationList(
        String sortBy, String order, String source, Integer page, Integer size
    ) {
        List<NotificationListResponseDto> all = generateMockNotifications(60);

        // source 필터 적용
        if (source != null && !source.isBlank()) {
            String src = source.trim();
            boolean validSource = SOURCES.stream().anyMatch(sv -> sv.equalsIgnoreCase(src));
            if (validSource) {
                all = all.stream()
                    .filter(n -> src.equalsIgnoreCase(String.valueOf(n.getSource())))
                    .collect(Collectors.toList());
            }
        }

        // 정렬 적용
        if ("createdAt".equalsIgnoreCase(sortBy)) {
            Comparator<NotificationListResponseDto> cmp = Comparator.comparing(
                NotificationListResponseDto::getCreatedAt,
                Comparator.nullsLast(Comparator.naturalOrder()));
            if ("desc".equalsIgnoreCase(order)) {
                cmp = cmp.reversed();
            }
            all = all.stream().sorted(cmp).collect(Collectors.toList());
        }

        // 페이지네이션 적용
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

        return PageResponseDto.<NotificationListResponseDto>builder()
            .items(pageItems)
            .page(pageDto)
            .build();
    }

    private NotificationCountResponseDto createFallbackNotificationCount(String status) {
        int count;
        if ("UNREAD".equalsIgnoreCase(status)) {
            count = 3;
        } else if ("READ".equalsIgnoreCase(status)) {
            count = 12;
        } else {
            count = 15;
        }

        return NotificationCountResponseDto.builder()
            .count(count)
            .build();
    }

    private List<NotificationListResponseDto> generateMockNotifications(int size) {
        List<NotificationListResponseDto> list = new ArrayList<>();

        LocalDateTime base = LocalDateTime.now();

        for (int i = 0; i < size; i++) {
            String source = SOURCES.get(i % SOURCES.size());
            String linkType = LINK_TYPES.get(i % LINK_TYPES.size());
            NotificationListResponseDto m = NotificationListResponseDto.builder()
                .notificationId(UuidCreator.getTimeOrderedEpoch())
                .notificationTitle(
                    source.equals("SD") ? "판매 오더 SO-2024-%03d가 생성되었습니다".formatted(100 + i)
                        : "생산 오더 PO-2024-%03d이 승인되었습니다".formatted(100 + i))
                .notificationMessage(
                    source.equals("SD") ? "판매 오더 SO-2024-%03d가 생성되었습니다".formatted(100 + i)
                        : "생산 오더 PO-2024-%03d이 승인되었습니다".formatted(100 + i))
                .linkType(linkType)
                .linkId(UuidCreator.getTimeOrderedEpoch())
                .source(source)
                .createdAt(base.plusMinutes(i * 3L))
                .build();
            list.add(m);
        }

        return list;
    }
}
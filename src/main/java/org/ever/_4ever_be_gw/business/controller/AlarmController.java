package org.ever._4ever_be_gw.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.ever._4ever_be_gw.common.dto.PageDto;
import org.ever._4ever_be_gw.common.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/alarm/notifications")
@Tag(name = "알림", description = "알림(Notification) API - 목업")
public class AlarmController {

    private static final List<String> SOURCES = List.of("PR", "SD", "IM", "FCM", "HRM", "PP", "CUS", "SUP");
    private static final List<String> LINK_TYPES = List.of(
        "PURCHASE_REQUISITION", "PURCHASE_ORDER", "PR_ETC",
        "QUOTATION", "SALES_ORDER", "SD_ETC",
        "IM_ETC", "SALES_INVOICE", "PURCHASE_INVOICE",
        "FCM_ETC", "HRM_ETC", "ESTIMATE", "INSUFFICIENT_STOCK", "PP_ETC"
    );

    // ===== 알림 목록 조회 =====
    @GetMapping("/list")
    @Operation(
        summary = "알림 목록 조회",
        description = "알림 목록을 페이징/정렬/필터와 함께 조회합니다."
    )
    public ResponseEntity<ApiResponse<Map<String, Object>>> getNotificationList(
        @RequestParam(name = "sortBy", required = false, defaultValue = "createdAt") String sortBy,
        @RequestParam(name = "order", required = false, defaultValue = "desc") String order,
        @RequestParam(name = "source", required = false) String source,
        @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
        @RequestParam(name = "size", required = false, defaultValue = "20") Integer size
    ) {
        int p = page == null || page < 0 ? 0 : page;
        int s = size == null || size < 1 ? 20 : size;

        List<Map<String, Object>> all = generateMockNotifications(60);

        // source 필터
        if (source != null && !source.isBlank()) {
            all = all.stream().filter(n -> source.equalsIgnoreCase(String.valueOf(n.get("source")))).collect(Collectors.toList());
        }

        // 정렬 (createdAt만 지원)
        if ("createdAt".equalsIgnoreCase(sortBy)) {
            Comparator<Map<String, Object>> cmp = Comparator.comparing(m -> (LocalDateTime) m.get("createdAt"));
            if ("desc".equalsIgnoreCase(order)) cmp = cmp.reversed();
            all = all.stream().sorted(cmp).collect(Collectors.toList());
        }

        int total = all.size();
        int fromIdx = Math.min(p * s, total);
        int toIdx = Math.min(fromIdx + s, total);
        List<Map<String, Object>> pageItems = all.subList(fromIdx, toIdx).stream()
            .map(this::serializeNotification)
            .collect(Collectors.toList());

        int totalPages = s == 0 ? 0 : (int) Math.ceil((double) total / s);
        boolean hasNext = p + 1 < totalPages;
        PageDto pageDto = PageDto.builder()
            .number(p)
            .size(s)
            .totalElements(total)
            .totalPages(totalPages)
            .hasNext(hasNext)
            .build();

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("items", pageItems);
        data.put("page", pageDto);

        return ResponseEntity.ok(ApiResponse.success(data, "알림 목록을 성공적으로 조회했습니다.", HttpStatus.OK));
    }

    // ===== 알림 갯수 조회 =====
    @GetMapping("/count")
    @Operation(summary = "알림 갯수 조회", description = "상태별(READ/UNREAD) 알림 갯수를 조회합니다.")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getNotificationCount(
        @RequestParam(name = "status", required = false) String status
    ) {
        // 단순 목업: status가 UNREAD면 3, READ면 12, 미지정이면 총 15로 가정
        int count;
        if ("UNREAD".equalsIgnoreCase(status)) count = 3;
        else if ("READ".equalsIgnoreCase(status)) count = 12;
        else count = 15;

        Map<String, Object> data = Map.of("count", count);
        String msg = (status == null || status.isBlank()) ? "전체 알림 갯수를 성공적으로 조회했습니다." :
            ("UNREAD".equalsIgnoreCase(status) ? "안 읽은 알림 갯수를 성공적으로 조회했습니다." : "읽은 알림 갯수를 성공적으로 조회했습니다.");
        return ResponseEntity.ok(ApiResponse.success(data, msg, HttpStatus.OK));
    }

    // ===== 알림 구독 요청 =====
    @PostMapping("/subscribe/{userId}")
    @Operation(summary = "알림 구독 요청", description = "사용자 구독을 등록합니다. (목업: data 없음)")
    public ResponseEntity<ApiResponse<Object>> subscribe(@PathVariable("userId") String userId) {
        return ResponseEntity.ok(ApiResponse.success(null, "알림 구독이 성공적으로 등록되었습니다.", HttpStatus.OK));
    }

    // ===== 알림 읽음 처리 (목록) =====
    @PatchMapping("/list/read")
    @Operation(summary = "알림 읽음 처리(목록)", description = "주어진 알림 ID 목록을 읽음 처리합니다. (목업)")
    public ResponseEntity<ApiResponse<Map<String, Object>>> markReadList(@RequestBody Map<String, List<String>> body) {
        List<String> ids = body == null ? List.of() : body.getOrDefault("notificationId", List.of());
        Map<String, Object> data = Map.of("processedCount", ids.size());
        String msg = ids.isEmpty() ? "읽음 처리할 알림이 없습니다." : "알림을 성공적으로 읽음 처리했습니다.";
        return ResponseEntity.ok(ApiResponse.success(data, msg, HttpStatus.OK));
    }

    // ===== 알림 읽음 처리 (전체) =====
    @PatchMapping("/all/read")
    @Operation(summary = "알림 읽음 처리(전체)", description = "모든 알림을 읽음 처리합니다. (목업)")
    public ResponseEntity<ApiResponse<Map<String, Object>>> markReadAll() {
        Map<String, Object> data = Map.of("processedCount", 15);
        return ResponseEntity.ok(ApiResponse.success(data, "모든 알림을 성공적으로 읽음 처리했습니다.", HttpStatus.OK));
    }

    // ===== 알림 읽음 처리 (단일) =====
    @PatchMapping("/{notificationId}/read")
    @Operation(summary = "알림 읽음 처리(단일)", description = "특정 알림을 읽음 처리합니다. (목업)")
    public ResponseEntity<ApiResponse<Map<String, Object>>> markReadOne(@PathVariable("notificationId") String notificationId) {
        Map<String, Object> data = Map.of("notificationId", notificationId, "status", "READ");
        return ResponseEntity.ok(ApiResponse.success(data, "알림을 성공적으로 읽음 처리했습니다.", HttpStatus.OK));
    }

    private List<Map<String, Object>> generateMockNotifications(int size) {
        List<Map<String, Object>> list = new ArrayList<>();
        LocalDateTime base = LocalDateTime.parse("2025-10-22T10:00:00");
        for (int i = 0; i < size; i++) {
            String source = SOURCES.get(i % SOURCES.size());
            String linkType = LINK_TYPES.get(i % LINK_TYPES.size());
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("notificationId", UUID.randomUUID());
            m.put("notificationTitle", source.equals("SD") ? "판매 오더 SO-2024-%03d가 생성되었습니다".formatted(100 + i) : "생산 오더 PO-2024-%03d이 승인되었습니다".formatted(100 + i));
            m.put("notificationMessage", String.valueOf(m.get("notificationTitle")));
            m.put("linkType", linkType);
            m.put("linkId", UUID.randomUUID());
            m.put("source", source);
            m.put("createdAt", base.plusMinutes(i * 3L));
            list.add(m);
        }
        return list;
    }

    private Map<String, Object> serializeNotification(Map<String, Object> raw) {
        Map<String, Object> s = new LinkedHashMap<>();
        s.put("notificationId", String.valueOf(raw.get("notificationId")));
        s.put("notificationTitle", raw.get("notificationTitle"));
        s.put("notificationMessage", raw.get("notificationMessage"));
        s.put("linkType", raw.get("linkType"));
        s.put("linkId", String.valueOf(raw.get("linkId")));
        s.put("source", raw.get("source"));
        s.put("createdAt", String.valueOf(raw.get("createdAt")));
        return s;
    }
}



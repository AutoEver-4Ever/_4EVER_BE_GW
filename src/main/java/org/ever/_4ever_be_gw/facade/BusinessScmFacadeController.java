package org.ever._4ever_be_gw.facade;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.ever._4ever_be_gw.common.exception.BusinessException;
import org.ever._4ever_be_gw.common.exception.ErrorCode;
import org.ever._4ever_be_gw.common.response.ApiResponse;
import org.ever._4ever_be_gw.facade.dto.DashboardWorkflowItemDto;
import org.ever._4ever_be_gw.facade.dto.DashboardWorkflowResponseDto;
import org.ever._4ever_be_gw.facade.dto.DashboardWorkflowTabDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/business/dashboard")
@Tag(name = "대시보드(워크플로우)", description = "대시보드 워크플로우 API")
public class BusinessScmFacadeController {

    private static final Set<String> MODULES = Set.of("SD", "MM", "IM", "PP", "HRM", "FIN");

    @GetMapping("/workflows")
    @Operation(
            summary = "대시보드 워크플로우 조회",
            description = "role별로 탭 2개를 함께 반환합니다. 각 탭은 최대 5개 항목을 제공합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"워크플로우를 조회했습니다.\",\n  \"data\": {\n    \"role\": \"MM_USER\",\n    \"tabs\": [\n      { \"tabCode\": \"PR\", \"items\": [ { \"id\": \"018f17e1-5c08-7a4b-8abc-1234567890ab\", \"title\": \"구매요청 검토\", \"code\": \"PR-2025-001\", \"statusCode\": \"PENDING\", \"date\": \"2025-10-22\" } ] },\n      { \"tabCode\": \"PO\", \"items\": [ { \"id\": \"018f17e1-5c09-7a4b-8abc-1234567890ab\", \"title\": \"발주서 진행\", \"code\": \"PO-2025-014\", \"statusCode\": \"ISSUED\", \"date\": \"2025-10-23\" } ] }\n    ]\n  }\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "잘못된 role",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "invalid_role", value = "{\n  \"status\": 400,\n  \"success\": false,\n  \"message\": \"잘못된 입력값입니다.\"\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "server_error", value = "{\n  \"status\": 500,\n  \"success\": false,\n  \"message\": \"워크플로우 조회 중 서버 오류가 발생했습니다.\"\n}"))
                    )
            }
    )
    public ResponseEntity<ApiResponse<DashboardWorkflowResponseDto>> getWorkflows(
            @Parameter(description = "사용자 역할 (예: SD_USER, MM_ADMIN, HRM_USER, FIN_ADMIN)", example = "SD_USER")
            @RequestParam(name = "role") String role
    ) {
        // role 형식 검증: <MODULE>_(USER|ADMIN), MODULE은 6개 중 하나
        if (role == null || role.isBlank()) {
            throw new BusinessException(ErrorCode.MISSING_INPUT_VALUE);
        }

        String[] parts = role.split("_");
        if (parts.length != 2 || !MODULES.contains(parts[0]) || !("USER".equals(parts[1]) || "ADMIN".equals(parts[1]))) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        // role prefix로 탭 코드/라벨 결정 (2개)
        String module = parts[0];
        List<DashboardWorkflowTabDto> tabs = switch (module) {
            case "SD" -> List.of(
                    DashboardWorkflowTabDto.builder().tabCode("QT").items(mockItems("SD", "QT")).build(),
                    DashboardWorkflowTabDto.builder().tabCode("SO").items(mockItems("SD", "SO")).build()
            );
            case "MM" -> List.of(
                    DashboardWorkflowTabDto.builder().tabCode("PR").items(mockItems("MM", "PR")).build(),
                    DashboardWorkflowTabDto.builder().tabCode("PO").items(mockItems("MM", "PO")).build()
            );
            case "IM" -> List.of(
                    DashboardWorkflowTabDto.builder().tabCode("GR").items(mockItems("IM", "GR")).build(),
                    DashboardWorkflowTabDto.builder().tabCode("GI").items(mockItems("IM", "GI")).build()
            );
            case "PP" -> List.of(
                    DashboardWorkflowTabDto.builder().tabCode("MES").items(mockItems("PP", "MES")).build(),
                    DashboardWorkflowTabDto.builder().tabCode("WO").items(mockItems("PP", "WO")).build()
            );
            case "HRM" -> List.of(
                    DashboardWorkflowTabDto.builder().tabCode("LV").items(mockItems("HRM", "LV")).build(),
                    DashboardWorkflowTabDto.builder().tabCode("ATT").items(mockItems("HRM", "ATT")).build()
            );
            case "FIN" -> List.of(
                    DashboardWorkflowTabDto.builder().tabCode("AR").items(mockItems("FIN", "AR")).build(),
                    DashboardWorkflowTabDto.builder().tabCode("AP").items(mockItems("FIN", "AP")).build()
            );
            default -> List.of();
        };

        DashboardWorkflowResponseDto data = DashboardWorkflowResponseDto.builder()
                .role(role)
                .tabs(tabs)
                .build();

        return ResponseEntity.ok(ApiResponse.success(data, "워크플로우를 조회했습니다.", HttpStatus.OK));
    }

    private List<DashboardWorkflowItemDto> mockItems(String module, String type) {
        List<DashboardWorkflowItemDto> list = new ArrayList<>();
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        for (int i = 1; i <= 5; i++) {
            String id = uuidV7();
            String code = type + "-2025-" + String.format("%03d", i);
            String title = makeTitle(type);
            String status = sampleStatus(type, i);
            list.add(DashboardWorkflowItemDto.builder()
                    .id(id)
                    .title(title)
                    .code(code)
                    .statusCode(status)
                    .date(now.plusDays(i).toLocalDate().toString())
                    .build());
        }
        return list;
    }

    private String makeTitle(String type) {
        return switch (type) {
            case "PR" -> "구매요청 검토";
            case "PO" -> "발주서 진행";
            case "QT" -> "견적 검토";
            case "SO" -> "주문 처리";
            case "GR" -> "입고 처리";
            case "GI" -> "출고 처리";
            case "MES" -> "생산 진행";
            case "WO" -> "작업지시 처리";
            case "AR" -> "매출 문서 처리";
            case "AP" -> "매입 문서 처리";
            case "LV" -> "휴가 승인";
            case "ATT" -> "근태 확인";
            default -> "업무 처리";
        };
    }

    private String sampleStatus(String type, int i) {
        return switch (type) {
            case "QT" -> i % 2 == 0 ? "REVIEW" : "APPROVAL";
            case "PR" -> i % 2 == 0 ? "PENDING" : "REVIEW";
            case "PO" -> i % 2 == 0 ? "ISSUED" : "APPROVAL_WAIT";
            case "SO" -> i % 2 == 0 ? "READY_FOR_SHIPMENT" : "IN_PRODUCTION";
            case "GR" -> "RECEIVING";
            case "GI" -> "DISPATCHING";
            case "MES", "WO" -> i % 2 == 0 ? "IN_PROGRESS" : "PLANNED";
            case "AR" -> i % 2 == 0 ? "OPEN" : "COLLECTING";
            case "AP" -> i % 2 == 0 ? "OPEN" : "PAYING";
            case "LV" -> i % 2 == 0 ? "WAITING" : "APPROVED";
            case "ATT" -> "CHECK";
            default -> "PENDING";
        };
    }

    // UUID v7 형태(시간 기반 정렬) 모킹 생성기
    private String uuidV7() {
        long ms = System.currentTimeMillis();
        String timeHex = String.format("%012x", ms); // 48-bit time

        String timeLow = timeHex.substring(4);      // 8 hex
        String timeMid = timeHex.substring(0, 4);   // 4 hex

        String randA = String.format("%03x", ThreadLocalRandom.current().nextInt(0x1000)); // 12 bits
        String timeHiAndVersion = "7" + randA; // version 7

        int rnd = ThreadLocalRandom.current().nextInt(0, 256);
        int variant = (rnd & 0x3F) | 0x80; // set '10' in top two bits
        String clockSeqHiAndReserved = String.format("%02x", variant);
        String clockSeqLow = String.format("%02x", ThreadLocalRandom.current().nextInt(0, 256));

        long nodeRand = ThreadLocalRandom.current().nextLong(0, 1L << 48);
        String node = String.format("%012x", nodeRand);

        return timeLow + "-" + timeMid + "-" + timeHiAndVersion + "-" + clockSeqHiAndReserved + clockSeqLow + "-" + node;
    }
}

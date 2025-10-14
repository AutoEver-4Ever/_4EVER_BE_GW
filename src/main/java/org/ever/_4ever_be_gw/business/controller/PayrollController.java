package org.ever._4ever_be_gw.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.ever._4ever_be_gw.common.exception.ErrorCode;
import org.ever._4ever_be_gw.common.exception.ValidationException;
import org.ever._4ever_be_gw.common.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/business/payroll")
@Tag(name = "Payroll Management", description = "급여 관리 API")
public class PayrollController {

    // 월별 사내 급여 목록 조회
    // GET /api/business/payroll?year=&month=&name=&department=&position=&page=&size=
    @GetMapping
    @Operation(
        summary = "월별 급여 목록 조회",
        description = "월별 사내 급여 명세서 목록을 조회합니다.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "성공",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"급여 명세서 목록 조회에 성공했습니다.\",\n  \"data\": {\n    \"content\": [{\n      \"paystubId\": 101,\n      \"employee\": {\n        \"employeeId\": 1,\n        \"employeeName\": \"김민수\",\n        \"departmentId\": 1,\n        \"department\": \"구매관리부\",\n        \"positionId\": 1,\n        \"position\": \"과장\"\n      },\n      \"pay\": {\n        \"basePay\": 4500000,\n        \"overtimePay\": 150000,\n        \"deduction\": 450000,\n        \"netPay\": 4200000,\n        \"status\": \"COMPLETED\"\n      }\n    }],\n    \"pageable\": {\n      \"pageNumber\": 0, \"pageSize\": 10\n    },\n    \"totalPages\": 3,\n    \"totalElements\": 25\n  }\n}"))
            )
        }
    )
    public ResponseEntity<ApiResponse<Object>> getMonthlyPayrollList(
        @Parameter(description = "연도", example = "2025")
        @RequestParam(name = "year") Integer year,
        @Parameter(description = "월(1~12)", example = "10")
        @RequestParam(name = "month") Integer month,
        @Parameter(description = "직원 이름(선택)")
        @RequestParam(name = "name", required = false) String employeeName,
        @Parameter(description = "부서 ID(선택)")
        @RequestParam(name = "department", required = false) Long departmentId,
        @Parameter(description = "직급 ID(선택)")
        @RequestParam(name = "position", required = false) Long positionId,
        @Parameter(description = "페이지 번호(0-base)")
        @RequestParam(name = "page", required = false) Integer page,
        @Parameter(description = "페이지 크기(최대 200)")
        @RequestParam(name = "size", required = false) Integer size
    ) {
        List<Map<String, String>> errors = new ArrayList<>();
        if (year == null || year < 1900 || year > 2100) {
            errors.add(Map.of("field", "year", "reason", "RANGE_1900_2100"));
        }
        if (month == null || month < 1 || month > 12) {
            errors.add(Map.of("field", "month", "reason", "RANGE_1_12"));
        }
        if (page != null && page < 0) {
            errors.add(Map.of("field", "page", "reason", "MIN_0"));
        }
        if (size != null && size > 200) {
            errors.add(Map.of("field", "size", "reason", "MAX_200"));
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(ErrorCode.VALIDATION_FAILED, errors);
        }

        int p = (page == null || page < 0) ? 0 : page;
        int s = (size == null || size < 1) ? 10 : size;

        Map<String, Object> pageData = generateMonthlyPayrollPageMock(year, month, p, s);
        return ResponseEntity.ok(ApiResponse.success(
            pageData, "급여 명세서 목록 조회에 성공했습니다.", HttpStatus.OK
        ));
    }

    // 월별 사내 급여 상세 조회
    // GET /api/business/payroll/employee/{paystubId}?year=&month=
    @GetMapping("employee/{paystubId}")
    @Operation(
        summary = "월별 급여 상세 조회",
        description = "월별 사내 급여 명세서 상세를 조회합니다.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "성공",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"급여 명세서 상세 조회에 성공했습니다.\",\n  \"data\": {\n    \"paystubId\": 1,\n    \"employee\": {\n      \"employeeId\": 1,\n      \"employeeCode\": \"EMP001\",\n      \"employeeName\": \"김민수\",\n      \"department\": \"개발팀\",\n      \"position\": \"과장\"\n    },\n    \"pay\": {\n      \"basePay\": 4500000,\n      \"basePayItem\": [{\n        \"itemContent\": \"정기 급여\",\n        \"itemSum\": 4200000\n      },{\n        \"itemContent\": \"직책 수당\",\n        \"itemSum\": 300000\n      }],\n      \"overtimePay\": 150000,\n      \"overtimePayItem\": [{\n        \"itemContent\": \"야간 근무 수당 (5시간)\",\n        \"itemSum\": 100000\n      },{\n        \"itemContent\": \"휴일 근무 수당 (2시간)\",\n        \"itemSum\": 50000\n      }],\n      \"deduction\": -450000,\n      \"deductionItem\": [{\n        \"itemContent\": \"국민연금\",\n        \"itemSum\": -200000\n      },{\n        \"itemContent\": \"건강보험\",\n        \"itemSum\": -150000\n      },{\n        \"itemContent\": \"소득세\",\n        \"itemSum\": -100000\n      }],\n      \"netPay\": 4200000\n    },\n    \"status\": \"COMPLETED\",\n    \"expectedDate\": \"2024-01-25\"\n  }\n}"))
            )
        }
    )
    public ResponseEntity<ApiResponse<Object>> getMonthlyPayrollDetail(
        @Parameter(description = "급여 명세서 ID", example = "1")
        @PathVariable("paystubId") Long paystubId
    ) {
        List<Map<String, String>> errors = new ArrayList<>();
        if (paystubId == null || paystubId < 1) {
            errors.add(Map.of("field", "paystubId", "reason", "MIN_1"));
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(ErrorCode.VALIDATION_FAILED, errors);
        }

        Map<String, Object> data = generateMonthlyPayrollDetailMock(paystubId);
        return ResponseEntity.ok(ApiResponse.success(
            data, "급여 명세서 상세 조회에 성공했습니다.", HttpStatus.OK
        ));
    }

    // ==================== Mock 생성 함수 ====================

    private Map<String, Object> generateMonthlyPayrollPageMock(
        int year, int month, int page, int size
    ) {
        List<Map<String, Object>> content = new ArrayList<>();

        String[] names = {"김민수", "이영희", "박철수"};
        String[] departments = {"구매관리부", "개발팀"};
        String[] positions = {"과장", "대리", "사원"};

        for (int i = 0; i < 3; i++) {
            int idx = i % names.length;
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("paystubId", 101 + i);

            Map<String, Object> employee = new LinkedHashMap<>();
            employee.put("employeeId", (long) (i + 1));
            employee.put("employeeName", names[idx]);
            employee.put("departmentId", (long) (idx + 1));
            employee.put("department", departments[idx % departments.length]);
            employee.put("positionId", (long) (idx + 1));
            employee.put("position", positions[idx % positions.length]);
            row.put("employee", employee);

            Map<String, Object> pay = new LinkedHashMap<>();
            int base = (i == 0 ? 4500000 : i == 1 ? 3800000 : 3000000);
            int overtime = (i == 1 ? 300000 : i == 2 ? 100000 : 150000);
            int deduction = (i == 0 ? 450000 : i == 1 ? 350000 : 250000);
            int net = base + overtime - deduction;
            pay.put("basePay", base);
            pay.put("overtimePay", overtime);
            pay.put("deduction", deduction);
            pay.put("netPay", net);
            pay.put("status", (i % 2 == 0 ? "PENDING" : "COMPLETED"));
            row.put("pay", pay);

            content.add(row);
        }

        Map<String, Object> pageable = new LinkedHashMap<>();
        Map<String, Object> sort = new LinkedHashMap<>();
        sort.put("sorted", false);
        sort.put("unsorted", true);
        sort.put("empty", true);
        pageable.put("sort", sort);
        pageable.put("offset", page * size);
        pageable.put("pageNumber", page);
        pageable.put("pageSize", size);
        pageable.put("paged", true);
        pageable.put("unpaged", false);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("content", content);
        response.put("pageable", pageable);
        response.put("totalPages", 3);
        response.put("totalElements", 25);
        response.put("last", page >= 2);
        response.put("size", size);
        response.put("number", page);
        response.put("sort", sort);
        response.put("numberOfElements", content.size());
        response.put("first", page == 0);
        response.put("empty", content.isEmpty());

        return response;
    }

    private Map<String, Object> generateMonthlyPayrollDetailMock(Long paystubId) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("paystubId", paystubId);

        Map<String, Object> employee = new LinkedHashMap<>();
        employee.put("employeeId", 1L);
        employee.put("employeeCode", "EMP001");
        employee.put("employeeName", "김민수");
        employee.put("department", "개발팀");
        employee.put("position", "과장");
        data.put("employee", employee);

        Map<String, Object> pay = new LinkedHashMap<>();
        pay.put("basePay", 4500000);
        List<Map<String, Object>> baseItems = new ArrayList<>();
        baseItems.add(Map.of("itemContent", "정기 급여", "itemSum", 4200000));
        baseItems.add(Map.of("itemContent", "직책 수당", "itemSum", 300000));
        pay.put("basePayItem", baseItems);

        pay.put("overtimePay", 150000);
        List<Map<String, Object>> overtimeItems = new ArrayList<>();
        overtimeItems.add(Map.of("itemContent", "야간 근무 수당 (5시간)", "itemSum", 100000));
        overtimeItems.add(Map.of("itemContent", "휴일 근무 수당 (2시간)", "itemSum", 50000));
        pay.put("overtimePayItem", overtimeItems);

        pay.put("deduction", -450000);
        List<Map<String, Object>> deductionItems = new ArrayList<>();
        deductionItems.add(Map.of("itemContent", "국민연금", "itemSum", -200000));
        deductionItems.add(Map.of("itemContent", "건강보험", "itemSum", -150000));
        deductionItems.add(Map.of("itemContent", "소득세", "itemSum", -100000));
        pay.put("deductionItem", deductionItems);

        pay.put("netPay", 4200000);
        data.put("pay", pay);

        data.put("status", "COMPLETED");
        data.put("expectedDate", LocalDateTime.now().withDayOfMonth(25));

        return data;
    }
}

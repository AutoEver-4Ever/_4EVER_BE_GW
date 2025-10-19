package org.ever._4ever_be_gw.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ever._4ever_be_gw.business.dto.*;
import org.ever._4ever_be_gw.business.dto.employee.EmployeeCreateRequestDto;
import org.ever._4ever_be_gw.business.dto.employee.EmployeeUpdateRequestDto;
import org.ever._4ever_be_gw.common.dto.stats.StatsMetricsDto;
import org.ever._4ever_be_gw.common.dto.stats.StatsResponseDto;
import org.ever._4ever_be_gw.common.dto.PageDto;
import org.ever._4ever_be_gw.common.exception.BusinessException;
import org.ever._4ever_be_gw.common.exception.ErrorCode;
import org.ever._4ever_be_gw.common.exception.ValidationException;
import org.ever._4ever_be_gw.common.response.ApiResponse;
import org.ever._4ever_be_gw.scmpp.dto.PeriodStatDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/business/hrm")
@Tag(name = "인사관리(HRM)", description = "인사 관리 API")
public class HrmController {

    // ==================== 인적자원 통계 ====================

    @GetMapping("/statistics")
    @Operation(
        summary = "HRM 통계 조회",
        description = "기간별 인적자원 통계 정보를 조회합니다.",
            responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "성공",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"대시보드 정보를 성공적으로 조회했습니다.\",\n  \"data\": {\n    \"week\": {\n      \"total_employee_count\": { \"value\": 100, \"delta_rate\": 0.012 },\n      \"new_employee_count\": { \"value\": 2, \"delta_rate\": 0.045 },\n      \"ongoing_program_count\": { \"value\": 8, \"delta_rate\": 0.038 },\n      \"completed_program_count\": { \"value\": 3, \"delta_rate\": 0.025 }\n    },\n    \"month\": {\n      \"total_employee_count\": { \"value\": 100, \"delta_rate\": 0.018 },\n      \"new_employee_count\": { \"value\": 10, \"delta_rate\": 0.062 },\n      \"ongoing_program_count\": { \"value\": 15, \"delta_rate\": 0.041 },\n      \"completed_program_count\": { \"value\": 5, \"delta_rate\": 0.033 }\n    }\n  }\n}"))
            )
        }
    )
    public ResponseEntity<ApiResponse<StatsResponseDto<StatsMetricsDto>>> getEmployeeStatistics(
        @Parameter(description = "조회 기간 목록(콤마 구분)")
        @RequestParam(name = "periods", required = false) String periods
    ) {
        final Set<String> allowedPeriods = Set.of("week", "month", "quarter", "year");

        List<String> requested = periods == null || periods.isBlank()
            ? List.of("week", "month", "quarter", "year")
            : Arrays.stream(periods.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .toList();

        List<String> invalid = requested.stream()
            .filter(p -> !allowedPeriods.contains(p))
            .toList();

        if (periods != null && !periods.isBlank() && (!invalid.isEmpty() || requested.stream().noneMatch(allowedPeriods::contains))) {
            List<Map<String, String>> errors = List.of(
                Map.of("field", "periods", "reason", "ALLOWED_VALUES: WEEK, MONTH, QUARTER, YEAR")
            );
            throw new ValidationException(ErrorCode.VALIDATION_FAILED, errors);
        }

        List<String> finalPeriods = requested.stream().filter(allowedPeriods::contains).toList();

        StatsResponseDto.StatsResponseDtoBuilder<StatsMetricsDto> builder = StatsResponseDto.<StatsMetricsDto>builder();
        if (finalPeriods.contains("week")) {
            builder.week(buildEmployeeMetrics(100, 0.012, 2, 0.045, 8, 0.038, 3, 0.025));
        }
        if (finalPeriods.contains("month")) {
            builder.month(buildEmployeeMetrics(100, 0.018, 10, 0.062, 15, 0.041, 5, 0.033));
        }
        if (finalPeriods.contains("quarter")) {
            builder.quarter(buildEmployeeMetrics(100, 0.021, 25, 0.074, 35, 0.058, 12, 0.049));
        }
        if (finalPeriods.contains("year")) {
            builder.year(buildEmployeeMetrics(100, 0.027, 45, 0.083, 60, 0.065, 28, 0.057));
        }

        StatsResponseDto<StatsMetricsDto> data = builder.build();
        return ResponseEntity.ok(ApiResponse.success(
            data, "대시보드 정보를 성공적으로 조회했습니다.", HttpStatus.OK
        ));
    }

    // ==================== 직원 관리 ====================

    @PostMapping("/employee/signup")
    @Operation(
        summary = "직원 신규 등록",
        description = "새로운 직원을 등록합니다.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "성공",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"직원 등록이 완료되었습니다.\",\n  \"data\": null\n}"))
            )
        }
    )
    public ResponseEntity<ApiResponse<Object>> signupEmployee(
        @Valid @RequestBody EmployeeCreateRequestDto requestDto
    ) {
        // 요청 데이터 로깅 (실제 구현에서는 서비스로 전달)
        System.out.println("직원 신규 등록 요청: " + requestDto);

        return ResponseEntity.ok(ApiResponse.success(
            null, "직원 등록이 완료되었습니다.", HttpStatus.OK
        ));
    }

    @PatchMapping("/employee/{employeeId}")
    @Operation(
        summary = "직원 정보 수정",
        description = "기존 직원의 정보를 수정합니다.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "성공",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"직원 정보 수정이 완료되었습니다.\",\n  \"data\": null\n}"))
            )
        }
    )
    public ResponseEntity<ApiResponse<Object>> updateEmployee(
        @Parameter(description = "직원 ID", example = "1")
        @PathVariable("employeeId") Long employeeId,
        @Valid @RequestBody EmployeeUpdateRequestDto requestDto
    ) {
        // 요청 데이터 로깅 (실제 구현에서는 서비스로 전달)
        System.out.println("직원 정보 수정 요청 - ID: " + employeeId + ", 데이터: " + requestDto);

        return ResponseEntity.ok(ApiResponse.success(
            null, "직원 정보 수정이 완료되었습니다.", HttpStatus.OK
        ));
    }

    @GetMapping("/employee")
    @Operation(
        summary = "직원 목록 조회",
        description = "직원 목록을 페이지네이션으로 조회합니다.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "성공",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"직원 목록을 조회했습니다.\",\n  \"data\": {\n    \"content\": [\n      {\n        \"employeeId\": 1,\n        \"employeeNumber\": \"EMP001\",\n        \"name\": \"김철수\",\n        \"email\": \"kim@company.com\",\n        \"phone\": \"010-1234-5678\",\n        \"position\": \"대리\",\n        \"department\": \"개발팀\",\n        \"status\": \"ACTIVE\",\n        \"statusLabel\": \"재직\",\n        \"hireDate\": \"2023-01-15\",\n        \"birthDate\": \"1990-05-20\",\n        \"address\": \"서울시 강남구\",\n        \"emergencyContact\": \"김영희\",\n        \"emergencyPhone\": \"010-9876-5432\",\n        \"bankAccount\": \"1234567890\",\n        \"bankName\": \"국민은행\",\n        \"accountHolder\": \"김철수\",\n        \"createdAt\": \"2023-01-15\",\n        \"updatedAt\": \"2024-01-15\",\n        \"skills\": [\"Java\", \"Spring Boot\", \"React\"],\n        \"managerName\": \"박영수\",\n        \"managerId\": 2,\n        \"workLocation\": \"본사\",\n        \"employmentType\": \"정규직\"\n      }\n    ],\n    \"page\": {\n      \"number\": 0,\n      \"size\": 20,\n      \"totalElements\": 150,\n      \"totalPages\": 8,\n      \"hasNext\": true\n    }\n  }\n}"))
            )
        }
    )
    public ResponseEntity<ApiResponse<Object>> getEmployees(
        @Parameter(description = "부서 필터")
        @RequestParam(name = "department", required = false) Long department,
        @Parameter(description = "직급 필터")
        @RequestParam(name = "position", required = false) Long position,
        @Parameter(description = "이름 검색")
        @RequestParam(name = "name", required = false) String name,
        @Parameter(description = "페이지 번호(0-base)")
        @RequestParam(name = "page", required = false) Integer page,
        @Parameter(description = "페이지 크기(최대 200)")
        @RequestParam(name = "size", required = false) Integer size
    ) {
        // 검증
        List<Map<String, String>> errors = new ArrayList<>();
        // TODO department, position 존재 여부 체크
        if (page != null && page < 0) {
            errors.add(Map.of("field", "page", "reason", "MIN_0"));
        }
        if (size != null && size > 200) {
            errors.add(Map.of("field", "size", "reason", "MAX_200"));
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(ErrorCode.VALIDATION_FAILED, errors);
        }

        // 기본값 처리
        int p = (page == null || page < 0) ? 0 : page;
        int s = (size == null || size < 1) ? 20 : size;

        // Mock 데이터 생성
        List<Map<String, Object>> content = generateEmployeeListMockData(s);

        int totalElements = 150;
        int totalPages = s == 0 ? 0 : (int) Math.ceil((double) totalElements / s);
        PageDto pageInfo = PageDto.builder()
            .number(p)
            .size(s)
            .totalElements(totalElements)
            .totalPages(totalPages)
            .hasNext(p + 1 < totalPages)
            .build();

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("content", content);
        data.put("page", pageInfo);

        return ResponseEntity.ok(ApiResponse.success(
            data, "직원 목록을 조회했습니다.", HttpStatus.OK
        ));
    }

    @GetMapping("/employees/{employeeId}")
    @Operation(
        summary = "직원 상세 조회",
        description = "직원 상세 정보를 조회합니다.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "성공",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"직원 상세 정보를 조회했습니다.\",\n  \"data\": {\n    \"employeeId\": 1,\n    \"employeeNumber\": \"EMP001\",\n    \"name\": \"김철수\",\n    \"email\": \"kim@company.com\",\n    \"phone\": \"010-1234-5678\",\n    \"position\": \"대리\",\n    \"department\": \"개발팀\",\n    \"status\": \"ACTIVE\",\n    \"statusLabel\": \"재직\",\n    \"hireDate\": \"2023-01-15\",\n    \"birthDate\": \"1990-05-20\",\n    \"address\": \"서울시 강남구 테헤란로 123\",\n    \"emergencyContact\": \"김영희\",\n    \"emergencyPhone\": \"010-9876-5432\",\n    \"bankAccount\": \"1234567890\",\n    \"bankName\": \"국민은행\",\n    \"accountHolder\": \"김철수\",\n    \"createdAt\": \"2023-01-15\",\n    \"updatedAt\": \"2024-01-15\",\n    \"skills\": [\"Java\", \"Spring Boot\", \"React\", \"MySQL\"],\n    \"managerName\": \"박영수\",\n    \"managerId\": 2,\n    \"workLocation\": \"본사\",\n    \"employmentType\": \"정규직\"\n  }\n}"))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "직원을 찾을 수 없음",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "not_found", value = "{\n  \"status\": 404,\n  \"success\": false,\n  \"message\": \"해당 직원을 찾을 수 없습니다.\",\n  \"errors\": { \"code\": 2001 }\n}"))
            )
        }
    )
    public ResponseEntity<ApiResponse<Object>> getEmployeeDetail(
        @Parameter(description = "직원 ID", example = "1")
        @PathVariable("employeeId") Long employeeId
    ) {
        if (employeeId == null || employeeId < 1 || employeeId > 100) {
            throw new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND);
        }

        Map<String, Object> data = generateEmployeeDetailMockData(employeeId);
        return ResponseEntity.ok(ApiResponse.success(
            data, "직원 상세 정보를 조회했습니다.", HttpStatus.OK
        ));
    }

    // ==================== 부서 관리 ====================

    @GetMapping("/departments")
    @Operation(
        summary = "부서 목록 조회",
        description = "부서 목록을 조회합니다.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "성공",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"부서 목록을 조회했습니다.\",\n  \"data\": {\n    \"total\": 8,\n    \"page\": 1,\n    \"size\": 10,\n    \"totalPages\": 1,\n    \"hasNext\": false,\n    \"hasPrev\": false,\n    \"departments\": [\n      {\n        \"departmentId\": 1,\n        \"departmentCode\": \"DEV\",\n        \"departmentName\": \"개발팀\",\n        \"description\": \"소프트웨어 개발 및 유지보수\",\n        \"managerName\": \"박영수\",\n        \"managerId\": 2,\n        \"location\": \"본사 3층\",\n        \"status\": \"ACTIVE\",\n        \"statusLabel\": \"활성\",\n        \"employeeCount\": 25,\n        \"budget\": 500000000.0,\n        \"budgetCurrency\": \"KRW\",\n        \"establishedDate\": \"2020-01-01\",\n        \"createdAt\": \"2020-01-01\",\n        \"updatedAt\": \"2024-01-01\",\n        \"responsibilities\": [\"소프트웨어 개발\", \"시스템 유지보수\", \"기술 연구\"],\n        \"parentDepartment\": \"IT본부\",\n        \"parentDepartmentId\": 1\n      }\n    ]\n  }\n}"))
            )
        }
    )
    public ResponseEntity<ApiResponse<Object>> getDepartments(
        @Parameter(description = "상태 필터: ACTIVE, INACTIVE")
        @RequestParam(name = "status", required = false) String status,
        @Parameter(description = "페이지(1-base)", example = "1")
        @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
        @Parameter(description = "페이지 크기(최대 200)", example = "10")
        @RequestParam(name = "size", required = false, defaultValue = "10") Integer size
    ) {
        // 검증
        List<Map<String, String>> errors = new ArrayList<>();
        if (status != null) {
            var allowed = Set.of("ACTIVE", "INACTIVE");
            if (!allowed.contains(status)) {
                errors.add(Map.of("field", "status", "reason", "ALLOWED_VALUES: ACTIVE, INACTIVE"));
            }
        }
        if (page != null && page < 1) {
            errors.add(Map.of("field", "page", "reason", "MIN_1"));
        }
        if (size != null && size > 200) {
            errors.add(Map.of("field", "size", "reason", "MAX_200"));
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(ErrorCode.VALIDATION_FAILED, errors);
        }

        List<Map<String, Object>> departments = generateDepartmentListMockData();

        int total = departments.size();
        int pageIndex = (page == null || page < 1) ? 0 : page - 1;
        int pageSize = (size == null || size < 1) ? total : size;
        int totalPages = pageSize == 0 ? 0 : (int) Math.ceil((double) total / pageSize);
        int fromIdx = Math.min(pageIndex * pageSize, total);
        int toIdx = Math.min(fromIdx + pageSize, total);
        List<Map<String, Object>> pageContent = departments.subList(fromIdx, toIdx);

        PageDto pageInfo = PageDto.builder()
            .number(pageIndex)
            .size(pageSize)
            .totalElements(total)
            .totalPages(totalPages)
            .hasNext(pageIndex + 1 < totalPages)
            .build();

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("total", total);
        data.put("departments", pageContent);
        data.put("page", pageInfo);

        return ResponseEntity.ok(ApiResponse.success(
            data, "부서 목록을 조회했습니다.", HttpStatus.OK
        ));
    }

    // ==================== 직급 관리 ====================

    @GetMapping("/positions")
    @Operation(
        summary = "직급 목록 조회",
        description = "직급 목록을 조회합니다.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "성공",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"직급 목록을 조회했습니다.\",\n  \"data\": [\n    {\n      \"positionId\": 1,\n      \"positionName\": \"사원\",\n      \"headCount\": 15,\n      \"payment\": 30000000\n    },\n    {\n      \"positionId\": 2,\n      \"positionName\": \"주임\",\n      \"headCount\": 12,\n      \"payment\": 35000000\n    }\n  ]\n}"))
            )
        }
    )
    public ResponseEntity<ApiResponse<Object>> getPositions() {
        // Mock 데이터 생성
        List<Map<String, Object>> positions = generatePositionListMockData();

        return ResponseEntity.ok(ApiResponse.success(
            positions, "직급 목록을 조회했습니다.", HttpStatus.OK
        ));
    }

    @GetMapping("/positions/{positionId}")
    @Operation(
        summary = "직급 상세 조회",
        description = "직급 상세 정보를 조회합니다.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "성공",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"직급 상세 정보를 조회했습니다.\",\n  \"data\": {\n    \"positionId\": 1,\n    \"positionCode\": \"STAFF\",\n    \"positionName\": \"사원\",\n    \"headCount\": 15,\n    \"payment\": 30000000,\n    \"employees\": [\n      {\n        \"employeeId\": 101,\n        \"employeeName\": \"김철수\",\n        \"departmentId\": 1,\n        \"departmentName\": \"개발팀\",\n        \"hireDate\": \"2020-01-01\"\n      }\n    ]\n  }\n}"))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "직급을 찾을 수 없음",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "not_found", value = "{\n  \"status\": 404,\n  \"success\": false,\n  \"message\": \"해당 직급을 찾을 수 없습니다.\",\n  \"errors\": { \"code\": 2002 }\n}"))
            )
        }
    )
    public ResponseEntity<ApiResponse<Object>> getPositionDetail(
        @Parameter(description = "직급 ID", example = "1")
        @PathVariable("positionId") Long positionId
    ) {
        if (positionId == null || positionId < 1 || positionId > 10) {
            throw new BusinessException(ErrorCode.POSITION_NOT_FOUND);
        }

        Map<String, Object> data = generatePositionDetailMockData(positionId);
        return ResponseEntity.ok(ApiResponse.success(
            data, "직급 상세 정보를 조회했습니다.", HttpStatus.OK
        ));
    }

    // ==================== 출퇴근 관리 ====================

    @GetMapping("/attendance")
    @Operation(
        summary = "출퇴근 기록 조회",
        description = "출퇴근 기록을 조회합니다.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "성공",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"출퇴근 기록을 조회했습니다.\",\n  \"data\": {\n    \"content\": [\n      {\n        \"attendanceId\": 1,\n        \"employeeId\": 1,\n        \"employeeName\": \"김철수\",\n        \"employeeNumber\": \"EMP001\",\n        \"attendanceDate\": \"2024-01-15\",\n        \"checkInTime\": \"09:00:00\",\n        \"checkOutTime\": \"18:00:00\",\n        \"status\": \"NORMAL\",\n        \"statusLabel\": \"정상\",\n        \"workType\": \"OFFICE\",\n        \"workTypeLabel\": \"사무실\",\n        \"location\": \"본사\",\n        \"notes\": \"\",\n        \"workingHours\": 8.0,\n        \"overtimeHours\": 0.0,\n        \"approvalStatus\": \"APPROVED\",\n        \"approvalStatusLabel\": \"승인\",\n        \"approverName\": \"박영수\",\n        \"approverId\": 2,\n        \"createdAt\": \"2024-01-15\",\n        \"updatedAt\": \"2024-01-15\",\n        \"department\": \"개발팀\",\n        \"position\": \"대리\",\n        \"isLate\": false,\n        \"isEarlyLeave\": false,\n        \"lateReason\": null,\n        \"earlyLeaveReason\": null\n      }\n    ],\n    \"page\": {\n      \"number\": 0,\n      \"size\": 20,\n      \"totalElements\": 500,\n      \"totalPages\": 25,\n      \"hasNext\": true\n    }\n  }\n}"))
            )
        }
    )
    public ResponseEntity<ApiResponse<Object>> getAttendance(
        @Parameter(description = "직원 ID")
        @RequestParam(name = "employeeId", required = false) Long employeeId,
        @Parameter(description = "시작일(YYYY-MM-DD)")
        @RequestParam(name = "startDate", required = false) String startDate,
        @Parameter(description = "종료일(YYYY-MM-DD)")
        @RequestParam(name = "endDate", required = false) String endDate,
        @Parameter(description = "상태 필터: NORMAL, LATE, EARLY_LEAVE, ABSENT")
        @RequestParam(name = "status", required = false) String status,
        @Parameter(description = "페이지 번호(0-base)")
        @RequestParam(name = "page", required = false) Integer page,
        @Parameter(description = "페이지 크기(최대 200)")
        @RequestParam(name = "size", required = false) Integer size
    ) {
        // 검증
        List<Map<String, String>> errors = new ArrayList<>();

        if (startDate != null) {
            try {
                LocalDate.parse(startDate);
            } catch (Exception e) {
                errors.add(Map.of("field", "startDate", "reason", "INVALID_DATE"));
            }
        }
        if (endDate != null) {
            try {
                LocalDate.parse(endDate);
            } catch (Exception e) {
                errors.add(Map.of("field", "endDate", "reason", "INVALID_DATE"));
            }
        }
        if (status != null) {
            var allowed = Set.of("NORMAL", "LATE", "EARLY_LEAVE", "ABSENT");
            if (!allowed.contains(status)) {
                errors.add(Map.of("field", "status", "reason",
                    "ALLOWED_VALUES: NORMAL, LATE, EARLY_LEAVE, ABSENT"));
            }
        }
        if (size != null && size > 200) {
            errors.add(Map.of("field", "size", "reason", "MAX_200"));
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(ErrorCode.VALIDATION_FAILED, errors);
        }

        // 기본값 처리
        int p = (page == null || page < 0) ? 0 : page;
        int s = (size == null || size < 1) ? 20 : size;

        // Mock 데이터 생성
        List<Map<String, Object>> content = generateAttendanceMockData(10);

        int totalElements = 500;
        int totalPages = s == 0 ? 0 : (int) Math.ceil((double) totalElements / s);
        PageDto pageInfo = PageDto.builder()
            .number(p)
            .size(s)
            .totalElements(totalElements)
            .totalPages(totalPages)
            .hasNext(p + 1 < totalPages)
            .build();

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("content", content);
        data.put("page", pageInfo);

        return ResponseEntity.ok(ApiResponse.success(
            data, "출퇴근 기록을 조회했습니다.", HttpStatus.OK
        ));
    }

    // ==================== 휴가 관리 ====================

    @GetMapping("/leave-requests")
    @Operation(
        summary = "휴가 신청 목록 조회",
        description = "휴가 신청 목록을 조회합니다.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "성공",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"휴가 신청 목록을 조회했습니다.\",\n  \"data\": {\n    \"content\": [\n      {\n        \"leaveRequestId\": 1,\n        \"employeeId\": 1,\n        \"employeeName\": \"김철수\",\n        \"employeeNumber\": \"EMP001\",\n        \"leaveType\": \"ANNUAL\",\n        \"leaveTypeLabel\": \"연차\",\n        \"reason\": \"개인사정\",\n        \"description\": \"가족 행사 참석\",\n        \"startDate\": \"2024-01-20\",\n        \"endDate\": \"2024-01-22\",\n        \"totalDays\": 3.0,\n        \"status\": \"PENDING\",\n        \"statusLabel\": \"대기\",\n        \"approvalStatus\": \"PENDING\",\n        \"approvalStatusLabel\": \"승인대기\",\n        \"approverName\": \"박영수\",\n        \"approverId\": 2,\n        \"approverComment\": null,\n        \"requestDate\": \"2024-01-15\",\n        \"approvalDate\": null,\n        \"createdAt\": \"2024-01-15\",\n        \"updatedAt\": \"2024-01-15\",\n        \"department\": \"개발팀\",\n        \"position\": \"대리\",\n        \"emergencyContact\": \"김영희\",\n        \"emergencyPhone\": \"010-9876-5432\",\n        \"isPaidLeave\": true,\n        \"remainingLeaveDays\": 12.0,\n        \"attachmentUrl\": null,\n        \"attachments\": []\n      }\n    ],\n    \"page\": {\n      \"number\": 0,\n      \"size\": 20,\n      \"totalElements\": 80,\n      \"totalPages\": 4,\n      \"hasNext\": true\n    }\n  }\n}"))
            )
        }
    )
    public ResponseEntity<ApiResponse<Object>> getLeaveRequests(
        @Parameter(description = "직원 ID")
        @RequestParam(name = "employeeId", required = false) Long employeeId,
        @Parameter(description = "휴가 유형: ANNUAL, SICK, PERSONAL, MATERNITY, PATERNITY")
        @RequestParam(name = "leaveType", required = false) String leaveType,
        @Parameter(description = "상태 필터: PENDING, APPROVED, REJECTED")
        @RequestParam(name = "status", required = false) String status,
        @Parameter(description = "페이지 번호(0-base)")
        @RequestParam(name = "page", required = false) Integer page,
        @Parameter(description = "페이지 크기(최대 200)")
        @RequestParam(name = "size", required = false) Integer size
    ) {
        // 검증
        List<Map<String, String>> errors = new ArrayList<>();
        if (leaveType != null) {
            var allowed = Set.of("ANNUAL", "SICK", "PERSONAL", "MATERNITY", "PATERNITY");
            if (!allowed.contains(leaveType)) {
                errors.add(Map.of("field", "leaveType", "reason",
                    "ALLOWED_VALUES: ANNUAL, SICK, PERSONAL, MATERNITY, PATERNITY"));
            }
        }
        if (status != null) {
            var allowed = Set.of("PENDING", "APPROVED", "REJECTED");
            if (!allowed.contains(status)) {
                errors.add(Map.of("field", "status", "reason",
                    "ALLOWED_VALUES: PENDING, APPROVED, REJECTED"));
            }
        }
        if (size != null && size > 200) {
            errors.add(Map.of("field", "size", "reason", "MAX_200"));
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(ErrorCode.VALIDATION_FAILED, errors);
        }

        // 기본값 처리
        int p = (page == null || page < 0) ? 0 : page;
        int s = (size == null || size < 1) ? 20 : size;

        // Mock 데이터 생성
        List<Map<String, Object>> content = generateLeaveRequestMockData(10);

        int totalElements = 80;
        int totalPages = s == 0 ? 0 : (int) Math.ceil((double) totalElements / s);
        PageDto pageInfo = PageDto.builder()
            .number(p)
            .size(s)
            .totalElements(totalElements)
            .totalPages(totalPages)
            .hasNext(p + 1 < totalPages)
            .build();

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("content", content);
        data.put("page", pageInfo);

        return ResponseEntity.ok(ApiResponse.success(
            data, "휴가 신청 목록을 조회했습니다.", HttpStatus.OK
        ));
    }

    // ==================== Mock 데이터 생성 메서드들 ====================

    // ==================== 인적자원 통계 Mock 데이터 생성 ====================

    private StatsMetricsDto buildEmployeeMetrics(
        long totalEmployeeCount,
        double totalEmployeeDelta,
        long newEmployeeCount,
        double newEmployeeDelta,
        long ongoingProgramCount,
        double ongoingProgramDelta,
        long completedProgramCount,
        double completedProgramDelta
    ) {
        return StatsMetricsDto.builder()
            .put("total_employee_count", PeriodStatDto.builder()
                .value(totalEmployeeCount)
                .deltaRate(BigDecimal.valueOf(totalEmployeeDelta))
                .build())
            .put("new_employee_count", PeriodStatDto.builder()
                .value(newEmployeeCount)
                .deltaRate(BigDecimal.valueOf(newEmployeeDelta))
                .build())
            .put("ongoing_program_count", PeriodStatDto.builder()
                .value(ongoingProgramCount)
                .deltaRate(BigDecimal.valueOf(ongoingProgramDelta))
                .build())
            .put("completed_program_count", PeriodStatDto.builder()
                .value(completedProgramCount)
                .deltaRate(BigDecimal.valueOf(completedProgramDelta))
                .build())
            .build();
    }

    // ==================== 직원 정보(Employee) Mock 데이터 생성 메서드들 ====================

    private List<Map<String, Object>> generateEmployeePartMockData(int count) {
        List<Map<String, Object>> employees = new ArrayList<>();
        String[] names = {"김철수", "박영수", "이영희", "최민수", "정수진", "한지훈", "송미영", "강동현", "윤서연", "임태호"};
        String[] positions = {"대리", "과장", "차장", "부장", "사원", "주임", "선임연구원", "연구원", "수석연구원", "책임연구원"};
        String[] departments = {"개발팀", "기획팀", "마케팅팀", "인사팀", "재무팀", "영업팀", "디자인팀", "QA팀", "운영팀",
            "연구개발팀"};

        for (int i = 0; i < count; i++) {
            Map<String, Object> employee = new LinkedHashMap<>();
            employee.put("employeeId", i + 1);
            employee.put("employeeCode", String.format("EMP%03d", i + 1));
            employee.put("employeeName", names[i % names.length]);

            employee.put("positionId", (i % positions.length) + 1);
            employee.put("position", positions[i % positions.length]);
            employee.put("departmentId", (i % departments.length) + 1);
            employee.put("department", departments[i % departments.length]);

            employee.put("hireDate", LocalDate.now().minusMonths(12 + i));

            employees.add(employee);
        }
        return employees;
    }

    private List<Map<String, Object>> generateEmployeeListMockData(int count) {
        List<Map<String, Object>> employees = new ArrayList<>();
        String[] names = {"김철수", "박영수", "이영희", "최민수", "정수진", "한지훈", "송미영", "강동현", "윤서연", "임태호"};
        String[] positions = {"대리", "과장", "차장", "부장", "사원", "주임", "선임연구원", "연구원", "수석연구원", "책임연구원"};
        String[] departments = {"개발팀", "기획팀", "마케팅팀", "인사팀", "재무팀", "영업팀", "디자인팀", "QA팀", "운영팀",
            "연구개발팀"};

        for (int i = 0; i < count; i++) {
            Map<String, Object> employee = new LinkedHashMap<>();
            employee.put("employeeId", i + 1);
            employee.put("employeeCode", String.format("EMP%03d", i + 1));
            employee.put("employeeName", names[i % names.length]);

            employee.put("positionId", (i % positions.length) + 1);
            employee.put("position", positions[i % positions.length]);
            employee.put("departmentId", (i % departments.length) + 1);
            employee.put("department", departments[i % departments.length]);

            employee.put("hireDate", LocalDate.now().minusMonths(12 + i));
            employee.put("birthDate", LocalDate.of(1990 + (i % 10), (i % 12) + 1, (i % 28) + 1));
            employee.put("address", "서울시 강남구 테헤란로 " + (100 + i));
            employee.put("email", String.format("employee%d@company.com", i + 1));
            employee.put("phone", String.format("010-%04d-%04d", 1000 + i, 1000 + i));

            employees.add(employee);
        }
        return employees;
    }

    private Map<String, Object> generateEmployeeDetailMockData(Long employeeId) {
        Map<String, Object> employee = new LinkedHashMap<>();
        employee.put("employeeId", employeeId);
        employee.put("employeeCode", String.format("EMP%03d", employeeId));
        employee.put("employeeName", "김철수");

        employee.put("positionId", (employeeId % 10) + 1);
        employee.put("position", "대리");
        employee.put("departmentId", 1);
        employee.put("department", "개발팀");

        employee.put("hireDate", LocalDate.of(2023, 1, 15));
        employee.put("birthDate", LocalDate.of(1990, 5, 20));
        employee.put("email", "kim@company.com");
        employee.put("phone", "010-1234-5678");
        employee.put("address", "서울시 강남구 테헤란로 123");
        employee.put("academicHistory", "서울대학교 컴퓨터공학과 졸업");
        employee.put("careerHistory", "5년차 소프트웨어 개발자");

        return employee;
    }

    // ==================== 부서(Department) Mock 데이터 생성 메서드들 ====================

    // id 조회용
    private List<Map<String, Object>> generateSimpleDepartmentMockData(int count) {
        List<Map<String, Object>> departmentList = new ArrayList<>();
        String[] departments = {"개발팀", "기획팀", "마케팅팀", "인사팀", "재무팀", "영업팀", "디자인팀", "QA팀", "운영팀",
            "연구개발팀"};

        for (int i = 0; i < count; i++) {
            Map<String, Object> department = new LinkedHashMap<>();

            department.put("employeeId", i + 1);
            department.put("department", departments[i % departments.length]);

            departmentList.add(department);
        }
        return departmentList;
    }

    private List<Map<String, Object>> generateDepartmentListMockData() {
        List<Map<String, Object>> departments = new ArrayList<>();
        String[] names = {"개발팀", "기획팀", "마케팅팀", "인사팀", "재무팀", "영업팀", "디자인팀", "QA팀"};
        String[] managers = {"박영수", "김영희", "이민수", "최수진", "정지훈", "한미영", "송동현", "강서연"};

        for (int i = 0; i < names.length; i++) {
            Map<String, Object> dept = new LinkedHashMap<>();
            dept.put("departmentId", i + 1);
            dept.put("departmentName", names[i]);
            dept.put("managerId", i + 1);
            dept.put("managerName", managers[i]);
            dept.put("employeeCount", 20 + i * 5);

            departments.add(dept);
        }
        return departments;
    }

    private Map<String, Object> generateDepartmentDetailMockData() {
        Map<String, Object> dept = new LinkedHashMap<>();
        dept.put("departmentId", 1);
        dept.put("departmentCode", "DEV");
        dept.put("departmentName", "개발팀");
        dept.put("managerId", 1);
        dept.put("managerName", "박영수");
        dept.put("employeeCount", 25);
        dept.put("establishedDate", LocalDate.now());
        dept.put("description", "소프트웨어 개발 및 유지보수");
        dept.put("employees", generateEmployeePartMockData(5)); // 부서 소속 직원 5명

        return dept;
    }

    // ==================== 직급(Position) Mock 데이터 생성 메서드들 ====================

    // id 조회용
    private List<Map<String, Object>> generateSimplePositionMockData(int count) {
        List<Map<String, Object>> positionList = new ArrayList<>();
        String[] positions = {"대리", "과장", "차장", "부장", "사원", "주임", "선임연구원", "연구원", "수석연구원", "책임연구원"};

        for (int i = 0; i < count; i++) {
            Map<String, Object> position = new LinkedHashMap<>();
            position.put("employeeId", i + 1);
            position.put("position", positions[i % positions.length]);

            positionList.add(position);
        }
        return positionList;
    }

    private List<Map<String, Object>> generatePositionListMockData() {
        List<Map<String, Object>> positions = new ArrayList<>();
        String[] positionNames = {"사원", "주임", "대리", "과장", "차장", "부장", "이사", "상무", "전무", "사장"};
        int[] headCounts = {15, 12, 10, 8, 6, 4, 3, 2, 1, 1};
        int[] annualSalaries = {30000000, 35000000, 40000000, 50000000, 60000000, 70000000,
            80000000, 90000000, 100000000, 120000000};

        for (int i = 0; i < positionNames.length; i++) {
            Map<String, Object> position = new LinkedHashMap<>();
            position.put("positionId", i + 1);
            position.put("positionName", positionNames[i]);
            position.put("headCount", headCounts[i]);
            position.put("payment", annualSalaries[i]);

            positions.add(position);
        }
        return positions;
    }

    private Map<String, Object> generatePositionDetailMockData(Long positionId) {
        Map<String, Object> position = new LinkedHashMap<>();
        String[] positionNames = {"사원", "주임", "대리", "과장", "차장", "부장", "이사", "상무", "전무", "사장"};
        String[] positionCodes = {"STAFF", "ASSISTANT", "ASSOCIATE", "MANAGER", "SENIOR_MANAGER",
            "DIRECTOR", "EXECUTIVE", "SENIOR_EXECUTIVE", "VICE_PRESIDENT", "PRESIDENT"};
        int[] headCounts = {15, 12, 10, 8, 6, 4, 3, 2, 1, 1};
        int[] annualSalaries = {30000000, 35000000, 40000000, 50000000, 60000000, 70000000,
            80000000, 90000000, 100000000, 120000000};

        int index = (int) ((positionId - 1) % positionNames.length);

        position.put("positionId", positionId);
        position.put("positionCode", positionCodes[index]);
        position.put("positionName", positionNames[index]);
        position.put("headCount", headCounts[index]);
        position.put("payment", annualSalaries[index]);
        position.put("employees", generateEmployeePartMockData(5));

        return position;
    }

    // ==================== 출퇴근/휴가 Mock 생성 (컨트롤러 상단에서 사용) ====================

    private List<Map<String, Object>> generateAttendanceMockData(int count) {
        List<Map<String, Object>> attendance = new ArrayList<>();
        String[] names = {"김철수", "박영수", "이영희", "최민수", "정수진", "한지훈", "송미영", "강동현", "윤서연", "임태호"};
        String[] statuses = {"NORMAL", "LATE", "EARLY_LEAVE", "ABSENT"};
        String[] statusLabels = {"정상", "지각", "조기퇴근", "결근"};

        for (int i = 0; i < count; i++) {
            Map<String, Object> record = new LinkedHashMap<>();
            record.put("attendanceId", i + 1);
            record.put("employeeId", i + 1);
            record.put("employeeName", names[i % names.length]);
            record.put("employeeNumber", String.format("EMP%03d", i + 1));
            record.put("attendanceDate", LocalDate.now().minusDays(i));
            record.put("checkInTime", "09:00:00");
            record.put("checkOutTime", "18:00:00");
            record.put("status", statuses[i % statuses.length]);
            record.put("statusLabel", statusLabels[i % statusLabels.length]);
            record.put("workType", "OFFICE");
            record.put("workTypeLabel", "사무실");
            record.put("location", "본사");
            record.put("notes", "");
            record.put("workingHours", 8.0);
            record.put("overtimeHours", i % 2 == 0 ? 1.0 : 0.0);
            record.put("approvalStatus", "APPROVED");
            record.put("approvalStatusLabel", "승인");
            record.put("approverName", "박영수");
            record.put("approverId", 2L);
            record.put("createdAt", LocalDate.now().minusDays(i));
            record.put("updatedAt", LocalDate.now().minusDays(i));
            record.put("department", "개발팀");
            record.put("position", "대리");
            record.put("isLate", i % 4 == 1);
            record.put("isEarlyLeave", i % 4 == 2);
            attendance.add(record);
        }
        return attendance;
    }

    private List<Map<String, Object>> generateLeaveRequestMockData(int count) {
        List<Map<String, Object>> requests = new ArrayList<>();
        String[] names = {"김철수", "박영수", "이영희", "최민수", "정수진", "한지훈", "송미영", "강동현", "윤서연", "임태호"};
        String[] leaveTypes = {"ANNUAL", "SICK", "PERSONAL", "MATERNITY", "PATERNITY"};
        String[] leaveTypeLabels = {"연차", "병가", "개인사정", "출산휴가", "육아휴가"};
        String[] statuses = {"PENDING", "APPROVED", "REJECTED"};
        String[] statusLabels = {"대기", "승인", "반려"};

        for (int i = 0; i < count; i++) {
            Map<String, Object> request = new LinkedHashMap<>();
            request.put("leaveRequestId", i + 1);
            request.put("employeeId", i + 1);
            request.put("employeeName", names[i % names.length]);
            request.put("employeeNumber", String.format("EMP%03d", i + 1));
            request.put("leaveType", leaveTypes[i % leaveTypes.length]);
            request.put("leaveTypeLabel", leaveTypeLabels[i % leaveTypeLabels.length]);
            request.put("reason", "개인사정");
            request.put("description", "가족 행사 참석");
            request.put("startDate", LocalDate.now().plusDays(i + 1));
            request.put("endDate", LocalDate.now().plusDays(i + 3));
            request.put("totalDays", 3.0);
            request.put("status", statuses[i % statuses.length]);
            request.put("statusLabel", statusLabels[i % statusLabels.length]);
            request.put("approvalStatus", statuses[i % statuses.length]);
            request.put("approvalStatusLabel", statusLabels[i % statusLabels.length]);
            request.put("approverName", "박영수");
            request.put("approverId", 2L);
            request.put("requestDate", LocalDate.now().minusDays(i));
            request.put("approvalDate", i % 3 == 1 ? LocalDate.now().minusDays(i - 1) : null);
            request.put("createdAt", LocalDate.now().minusDays(i));
            request.put("updatedAt", LocalDate.now().minusDays(i));
            request.put("department", "개발팀");
            request.put("position", "대리");
            requests.add(request);
        }
        return requests;
    }

    // 월별 사내 급여 목록 조회
    // GET /api/business/payroll?year=&month=&name=&department=&position=&page=&size=
    @GetMapping("/payroll")
    @Operation(
        summary = "월별 급여 목록 조회",
        description = "월별 사내 급여 명세서 목록을 조회합니다.",
		responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "200",
				description = "성공",
				content = @Content(mediaType = "application/json",
					examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"급여 명세서 목록 조회에 성공했습니다.\",\n  \"data\": {\n    \"content\": [{\n      \"paystubId\": 101,\n      \"employee\": {\n        \"employeeId\": 1,\n        \"employeeName\": \"김민수\",\n        \"departmentId\": 1,\n        \"department\": \"구매관리부\",\n        \"positionId\": 1,\n        \"position\": \"과장\"\n      },\n      \"pay\": {\n        \"basePay\": 4500000,\n        \"overtimePay\": 150000,\n        \"deduction\": 450000,\n        \"netPay\": 4200000,\n        \"status\": \"COMPLETED\"\n      }\n    }],\n    \"page\": {\n      \"number\": 0,\n      \"size\": 10,\n      \"totalElements\": 25,\n      \"totalPages\": 3,\n      \"hasNext\": true\n    },\n    \"first\": true,\n    \"last\": false,\n    \"numberOfElements\": 3\n  }\n}"))
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

        int totalElements = 25;
        int totalPages = size <= 0 ? 0 : (int) Math.ceil((double) totalElements / size);
        PageDto pageInfo = PageDto.builder()
            .number(page)
            .size(size)
            .totalElements(totalElements)
            .totalPages(totalPages)
            .hasNext(page + 1 < totalPages)
            .build();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("content", content);
        response.put("page", pageInfo);
        response.put("first", page == 0);
        response.put("last", page >= totalPages - 1);
        response.put("numberOfElements", content.size());
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
        data.put("expectedDate", LocalDate.now().withDayOfMonth(25));

        return data;
    }

    // ==================== 출퇴근 관리 ====================

    @PatchMapping("/attendance/check-in")
    @Operation(
        summary = "출근 상태 변경",
        description = "직원의 출근 상태를 변경합니다.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "성공",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"출근 처리가 완료되었습니다.\",\n  \"data\": {\n    \"timerecordId\": 101,\n    \"employeeId\": 1,\n    \"checkInTime\": \"2025-01-15T09:00:00\",\n    \"status\": \"ON_TIME\"\n  }\n}"))
            )
        }
    )
    public ResponseEntity<ApiResponse<Object>> checkIn() {
        // Mock 데이터 생성
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("timerecordId", 101L);
        data.put("employeeId", 1L);
        data.put("checkInTime", LocalDateTime.now());
        data.put("status", "ON_TIME");

        return ResponseEntity.ok(ApiResponse.success(
            data, "출근 처리가 완료되었습니다.", HttpStatus.OK
        ));
    }

    @PatchMapping("/attendance/check-out")
    @Operation(
        summary = "퇴근 상태 변경",
        description = "직원의 퇴근 상태를 변경합니다.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "성공",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"퇴근 처리가 완료되었습니다.\",\n  \"data\": {\n    \"timerecordId\": 101,\n    \"employeeId\": 1,\n    \"checkOutTime\": \"2025-01-15T18:00:00\",\n    \"totalWorkMinutes\": 540,\n    \"overtimeMinutes\": 0\n  }\n}"))
            )
        }
    )
    public ResponseEntity<ApiResponse<Object>> checkOut() {
        // Mock 데이터 생성
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("timerecordId", 101L);
        data.put("employeeId", 1L);
        data.put("checkOutTime", LocalDateTime.now());
        data.put("totalWorkMinutes", 540);
        data.put("overtimeMinutes", 0);

        return ResponseEntity.ok(ApiResponse.success(
            data, "퇴근 처리가 완료되었습니다.", HttpStatus.OK
        ));
    }

    @PutMapping("/time-record/{timerecordId}")
    @Operation(
        summary = "출퇴근 기록 수정",
        description = "출퇴근 기록을 수정합니다.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "성공",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"출퇴근 기록 수정이 완료되었습니다.\",\n  \"data\": {\n    \"timerecordId\": 101,\n    \"employeeId\": 1,\n    \"inTime\": \"2025-01-15T09:00:00\",\n    \"outTime\": \"2025-01-15T18:00:00\",\n    \"attendanceStatus\": \"ON_TIME\"\n  }\n}"))
            )
        }
    )
    public ResponseEntity<ApiResponse<Object>> updateTimeRecord(
        @Parameter(description = "근태 기록 ID", example = "101")
        @PathVariable("timerecordId") Long timerecordId,
        @Valid @RequestBody TimeRecordUpdateRequestDto requestDto
    ) {
        // 요청 데이터 로깅
        System.out.println("출퇴근 기록 수정 요청 - ID: " + timerecordId + ", 데이터: " + requestDto);

        // Mock 데이터 생성
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("timerecordId", timerecordId);
        data.put("employeeId", 1L);
        data.put("inTime", requestDto.getInTime());
        data.put("outTime", requestDto.getOutTime());
        data.put("attendanceStatus", requestDto.getAttendanceStatus());

        return ResponseEntity.ok(ApiResponse.success(
            data, "출퇴근 기록 수정이 완료되었습니다.", HttpStatus.OK
        ));
    }

    // ==================== 휴가 관리 ====================

    @PostMapping("/leave/request")
    @Operation(
        summary = "휴가 신청",
        description = "새로운 휴가를 신청합니다.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "성공",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"휴가 신청이 완료되었습니다.\",\n  \"data\": {\n    \"leaveRequestId\": 201,\n    \"employeeId\": 101,\n    \"leaveType\": \"ANNUAL\",\n    \"startDate\": \"2025-10-15\",\n    \"endDate\": \"2025-10-18\",\n    \"status\": \"PENDING\"\n  }\n}"))
            )
        }
    )
    public ResponseEntity<ApiResponse<Object>> requestLeave(
        @Valid @RequestBody LeaveRequestDto requestDto
    ) {
        // 요청 데이터 로깅
        System.out.println("휴가 신청 요청: " + requestDto);

        // Mock 데이터 생성
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("leaveRequestId", 201L);
        data.put("employeeId", requestDto.getEmployeeId());
        data.put("leaveType", requestDto.getLeaveType());
        data.put("startDate", requestDto.getStartDate());
        data.put("endDate", requestDto.getEndDate());
        data.put("status", "PENDING");

        return ResponseEntity.ok(ApiResponse.success(
            data, "휴가 신청이 완료되었습니다.", HttpStatus.OK
        ));
    }

    @PatchMapping("/leave/request/{requestId}/release")
    @Operation(
        summary = "휴가 신청 승인",
        description = "휴가 신청을 승인합니다.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "성공",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"휴가 신청이 승인되었습니다.\",\n  \"data\": {\n    \"leaveRequestId\": 201,\n    \"status\": \"APPROVED\",\n    \"approvedAt\": \"2025-01-15\"\n  }\n}"))
            )
        }
    )
    public ResponseEntity<ApiResponse<Object>> approveLeaveRequest(
        @Parameter(description = "휴가 신청 ID", example = "201")
        @PathVariable("requestId") Long requestId
    ) {
        // 요청 데이터 로깅
        System.out.println("휴가 신청 승인 요청 - ID: " + requestId);

        // Mock 데이터 생성
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("leaveRequestId", requestId);
        data.put("status", "APPROVED");
        data.put("approvedAt", LocalDate.now());

        return ResponseEntity.ok(ApiResponse.success(
            data, "휴가 신청이 승인되었습니다.", HttpStatus.OK
        ));
    }

    @PatchMapping("/leave/request/{requestId}/reject")
    @Operation(
        summary = "휴가 신청 반려",
        description = "휴가 신청을 반려합니다.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "성공",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"휴가 신청이 반려되었습니다.\",\n  \"data\": {\n    \"leaveRequestId\": 201,\n    \"status\": \"REJECTED\",\n    \"rejectedAt\": \"2025-01-15\"\n  }\n}"))
            )
        }
    )
    public ResponseEntity<ApiResponse<Object>> rejectLeaveRequest(
        @Parameter(description = "휴가 신청 ID", example = "201")
        @PathVariable("requestId") Long requestId
    ) {
        // 요청 데이터 로깅
        System.out.println("휴가 신청 반려 요청 - ID: " + requestId);

        // Mock 데이터 생성
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("leaveRequestId", requestId);
        data.put("status", "REJECTED");
        data.put("rejectedAt", LocalDate.now());

        return ResponseEntity.ok(ApiResponse.success(
            data, "휴가 신청이 반려되었습니다.", HttpStatus.OK
        ));
    }

    // ==================== 기존 조회 API들 ====================
    // GET /api/business/tam/time-record?department=&position=&name=&date=&page=&size=
    @GetMapping("/time-record")
    @Operation(
        summary = "출퇴근 기록 목록 조회",
        description = "부서/직책/이름/일자로 출퇴근 기록을 조회합니다.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "성공",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"근태 기록 조회에 성공했습니다.\",\n  \"data\": {\n    \"content\": [{\n      \"timerecordId\": 101,\n      \"employee\": {\n        \"employeeId\": 1,\n        \"employeeName\": \"김민수\",\n        \"departmentId\": 1,\n        \"department\": \"개발팀\",\n        \"positionId\": 1,\n        \"position\": \"과장\"\n      },\n      \"workDate\": \"2025-10-13\",\n      \"checkInTime\": \"2025-10-13T08:50:10Z\",\n      \"checkOutTime\": \"2025-10-13T18:20:35Z\",\n      \"totalWorkMinutes\": 510,\n      \"overtimeMinutes\": 60,\n      \"status\": \"ON_TIME\"\n    }],\n    \"totalPages\": 5,\n    \"totalElements\": 98\n  }\n}"))
            )
        }
    )
    public ResponseEntity<ApiResponse<Object>> getTimeRecords(
        @Parameter(description = "부서 ID")
        @RequestParam(name = "department", required = false) Long departmentId,
        @Parameter(description = "직책 ID")
        @RequestParam(name = "position", required = false) Long positionId,
        @Parameter(description = "직원 이름")
        @RequestParam(name = "name", required = false) String employeeName,
        @Parameter(description = "검색 일자(YYYY-MM-DD)")
        @RequestParam(name = "date") String date,
        @Parameter(description = "페이지 번호(0-base)")
        @RequestParam(name = "page", required = false) Integer page,
        @Parameter(description = "페이지 크기(최대 200)")
        @RequestParam(name = "size", required = false) Integer size
    ) {
        List<Map<String, String>> errors = new ArrayList<>();
        if (date != null) {
            try {
                LocalDate.parse(date);
            } catch (DateTimeParseException e) {
                errors.add(Map.of("field", "date", "reason", "INVALID_DATE"));
            }
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
        int s = (size == null || size < 1) ? 20 : size;
        LocalDate baseDate = LocalDate.parse(date);

        Map<String, Object> data = generateTimeRecordPageMock(p, s, baseDate);
        return ResponseEntity.ok(ApiResponse.success(
            data, "근태 기록 조회에 성공했습니다.", HttpStatus.OK
        ));
    }

    // 출퇴근 기록 상세 조회
    // GET /api/business/tam/time-record/{timerecordId}
    @GetMapping("/time-record/{timerecordId}")
    @Operation(
        summary = "출퇴근 기록 상세 조회",
        description = "단일 출퇴근 기록 상세 정보를 조회합니다.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "성공",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"근태 기록 상세 정보 조회에 성공했습니다.\",\n  \"data\": {\n    \"timerecordId\": 1,\n    \"employee\": {\n      \"employeeId\": 1,\n      \"employeeCode\": \"EMP001\",\n      \"employeeName\": \"김민수\",\n      \"departmentId\": 1,\n      \"department\": \"개발팀\",\n      \"positionId\": 1,\n      \"position\": \"과장\"\n    },\n    \"workDate\": \"2025-10-07\",\n    \"checkInTime\": \"2025-10-07T08:50:10Z\",\n    \"checkOutTime\": \"2025-10-07T18:20:35Z\",\n    \"totalWorkMinutes\": 510,\n    \"overtimeMinutes\": 60,\n    \"status\": \"ON_TIME\"\n  }\n}"))
            )
        }
    )
    public ResponseEntity<ApiResponse<Object>> getTimeRecordDetail(
        @Parameter(description = "출퇴근 기록 ID", example = "1")
        @PathVariable("timerecordId") Long timerecordId
    ) {
        if (timerecordId == null || timerecordId < 1) {
            throw new ValidationException(ErrorCode.VALIDATION_FAILED,
                List.of(Map.of("field", "timerecordId", "reason", "MIN_1")));
        }

        Map<String, Object> data = generateTimeRecordDetailMock(timerecordId);
        return ResponseEntity.ok(ApiResponse.success(
            data, "근태 기록 상세 정보 조회에 성공했습니다.", HttpStatus.OK
        ));
    }

    // ==================== Mock 생성 함수 ====================

    // 휴가 신청 목록 조회
    // GET /api/business/tam/leave-request?department=&position=&name=&type=&page=&size=&sortOrder=
    @GetMapping("/leave-request")
    @Operation(
        summary = "휴가 신청 목록 조회",
        description = "부서/직책/이름/유형으로 휴가 신청 목록을 조회합니다.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "성공",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"휴가 신청 목록 조회에 성공했습니다.\",\n  \"data\": {\n    \"content\": [{\n      \"leaveRequestId\": 1,\n      \"employee\": {\n        \"employeeId\": 1,\n        \"employeeName\": \"김민수\",\n        \"department\": \"개발팀\",\n        \"position\": \"과장\"\n      },\n      \"leaveType\": \"ANNUAL\",\n      \"startDate\": \"2024-01-20\",\n      \"endDate\": \"2024-01-22\",\n      \"numberOfLeaveDays\": 3,\n      \"remainingLeaveDays\": 12\n    }],\n    \"totalPages\": 4,\n    \"totalElements\": 35\n  }\n}"))
            )
        }
    )
    public ResponseEntity<ApiResponse<Object>> getLeaveRequestList(
        @Parameter(description = "부서 ID")
        @RequestParam(name = "department", required = false) Long departmentId,
        @Parameter(description = "직책 ID")
        @RequestParam(name = "position", required = false) Long positionId,
        @Parameter(description = "직원 이름")
        @RequestParam(name = "name", required = false) String employeeName,
        @Parameter(description = "휴가 유형: ANNUAL, SICK")
        @RequestParam(name = "type", required = false) String leaveType,
        @Parameter(description = "정렬: DESC(최신순) 또는 ASC(오래된 순)")
        @RequestParam(name = "sortOrder", required = false, defaultValue = "DESC") String sortOrder,
        @Parameter(description = "페이지 번호(0-base)")
        @RequestParam(name = "page", required = false) Integer page,
        @Parameter(description = "페이지 크기(최대 200)")
        @RequestParam(name = "size", required = false) Integer size
    ) {
        List<Map<String, String>> errors = new ArrayList<>();
        if (leaveType != null) {
            var allowed = java.util.Set.of("ANNUAL", "SICK");
            if (!allowed.contains(leaveType)) {
                errors.add(Map.of("field", "type", "reason", "ALLOWED_VALUES: ANNUAL, SICK"));
            }
        }
        if (sortOrder != null) {
            var allowedSort = java.util.Set.of("ASC", "DESC");
            if (!allowedSort.contains(sortOrder)) {
                errors.add(Map.of("field", "sortOrder", "reason", "ALLOWED_VALUES: ASC, DESC"));
            }
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

        Map<String, Object> data = generateLeaveRequestPageMock(p, s, sortOrder);
        return ResponseEntity.ok(ApiResponse.success(
            data, "휴가 신청 목록 조회에 성공했습니다.", HttpStatus.OK
        ));
    }

    private Map<String, Object> generateLeaveRequestPageMock(int page, int size, String sortOrder) {
        List<Map<String, Object>> content = new ArrayList<>();

        // 정렬 방향은 목 데이터의 순서만 바꿔서 반영
        int[] ids = ("ASC".equalsIgnoreCase(sortOrder)) ? new int[]{1, 2, 3} : new int[]{3, 2, 1};

        // 1
        content.add(buildLeaveRow(ids[0], 1L, "김민수", "개발팀", "과장", "ANNUAL",
            LocalDate.of(2024, 1, 20), LocalDate.of(2024, 1, 22), 3, 12));
        // 2
        content.add(buildLeaveRow(ids[1], 8L, "이영희", "기획팀", "대리", "SICK",
            LocalDate.of(2024, 2, 10), LocalDate.of(2024, 2, 10), 1, 5));
        // 3
        content.add(buildLeaveRow(ids[2], 15L, "박서준", "마케팅팀", "팀장", "ANNUAL",
            LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 5), 5, 10));

        int totalElements = 35;
        int totalPages = size <= 0 ? 0 : (int) Math.ceil((double) totalElements / size);
        PageDto pageInfo = PageDto.builder()
            .number(page)
            .size(size)
            .totalElements(totalElements)
            .totalPages(totalPages)
            .hasNext(page + 1 < totalPages)
            .build();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("content", content);
        response.put("page", pageInfo);
        response.put("first", page == 0);
        response.put("last", page + 1 >= totalPages);
        response.put("numberOfElements", content.size());
        response.put("empty", content.isEmpty());
        return response;
    }

    private Map<String, Object> buildLeaveRow(
        int leaveRequestId,
        Long employeeId,
        String employeeName,
        String department,
        String position,
        String leaveType,
        LocalDate startDate,
        LocalDate endDate,
        int numberOfLeaveDays,
        int remainingLeaveDays
    ) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("leaveRequestId", leaveRequestId);

        Map<String, Object> employee = new LinkedHashMap<>();
        employee.put("employeeId", employeeId);
        employee.put("employeeName", employeeName);
        employee.put("department", department);
        employee.put("position", position);
        row.put("employee", employee);

        row.put("leaveType", leaveType);
        row.put("startDate", startDate.atStartOfDay());
        row.put("endDate", endDate.atStartOfDay());
        row.put("numberOfLeaveDays", numberOfLeaveDays);
        row.put("remainingLeaveDays", remainingLeaveDays);

        return row;
    }

    private Map<String, Object> generateTimeRecordPageMock(int page, int size, LocalDate baseDate) {
        List<Map<String, Object>> content = new ArrayList<>();
        String[] names = {"김민수", "이영희", "박철수"};
        String[] departments = {"개발팀", "기획팀"};
        String[] positions = {"과장", "대리", "사원"};
        int[] totalMinutes = {510, 470, 572};
        int[] overtime = {60, 0, 102};
        String[] statuses = {"ON_TIME", "LATE", "ON_TIME"};

        for (int i = 0; i < 3; i++) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("timerecordId", 101 + i);

            Map<String, Object> employee = new LinkedHashMap<>();
            employee.put("employeeId", (long) (i + 1));
            employee.put("employeeName", names[i]);
            employee.put("departmentId", (long) ((i % departments.length) + 1));
            employee.put("department", departments[i % departments.length]);
            employee.put("positionId", (long) ((i % positions.length) + 1));
            employee.put("position", positions[i % positions.length]);
            row.put("employee", employee);

            row.put("workDate", baseDate);
            row.put("checkInTime", LocalDateTime.of(baseDate.getYear(), baseDate.getMonthValue(),
                baseDate.getDayOfMonth(), 8 + i, 50 + (i * 5) % 60, 10));
            row.put("checkOutTime", LocalDateTime.of(baseDate.getYear(), baseDate.getMonthValue(),
                baseDate.getDayOfMonth(), 18, 20 - (i * 5) % 20, 35));
            row.put("totalWorkMinutes", totalMinutes[i]);
            row.put("overtimeMinutes", overtime[i]);
            row.put("status", statuses[i]);

            content.add(row);
        }

        int totalElements = 98;
        int totalPages = size <= 0 ? 0 : (int) Math.ceil((double) totalElements / size);
        PageDto pageInfo = PageDto.builder()
            .number(page)
            .size(size)
            .totalElements(totalElements)
            .totalPages(totalPages)
            .hasNext(page + 1 < totalPages)
            .build();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("content", content);
        response.put("page", pageInfo);
        response.put("first", page == 0);
        response.put("last", page + 1 >= totalPages);
        response.put("numberOfElements", content.size());
        response.put("empty", content.isEmpty());
        return response;
    }

    private Map<String, Object> generateTimeRecordDetailMock(Long timerecordId) {
        LocalDate baseDate = LocalDate.of(2025, 10, 7);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("timerecordId", timerecordId);

        Map<String, Object> employee = new LinkedHashMap<>();
        employee.put("employeeId", 1L);
        employee.put("employeeCode", "EMP001");
        employee.put("employeeName", "김민수");
        employee.put("departmentId", 1L);
        employee.put("department", "개발팀");
        employee.put("positionId", 1L);
        employee.put("position", "과장");
        data.put("employee", employee);

        data.put("workDate", baseDate);
        data.put("checkInTime", LocalDateTime.of(2025, 10, 7, 8, 50, 10));
        data.put("checkOutTime", LocalDateTime.of(2025, 10, 7, 18, 20, 35));
        data.put("totalWorkMinutes", 510);
        data.put("overtimeMinutes", 60);
        data.put("status", "ON_TIME");

        return data;
    }
    // ==================== 교육 신청 및 프로그램 관리 ====================

    @PostMapping("/employee/request")
    @Operation(
        summary = "교육 신청",
        description = "직원이 교육 프로그램에 신청합니다.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "성공",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"교육 신청이 완료되었습니다.\",\n  \"data\": {\n    \"requestId\": 301,\n    \"programId\": 1001,\n    \"employeeId\": 101,\n    \"status\": \"PENDING\"\n  }\n}"))
            )
        }
    )
    public ResponseEntity<ApiResponse<Object>> requestTraining(
        @Valid @RequestBody TrainingRequestDto requestDto
    ) {
        // 요청 데이터 로깅
        System.out.println("교육 신청 요청: " + requestDto);

        // Mock 데이터 생성
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("requestId", 301L);
        data.put("programId", requestDto.getProgramId());
        data.put("employeeId", 101L);
        data.put("status", "PENDING");

        return ResponseEntity.ok(ApiResponse.success(
            data, "교육 신청이 완료되었습니다.", HttpStatus.OK
        ));
    }

    @PostMapping("/program")
    @Operation(
        summary = "교육 프로그램 추가",
        description = "새로운 교육 프로그램을 추가합니다.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "성공",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"교육 프로그램이 추가되었습니다.\",\n  \"data\": {\n    \"programId\": 2001,\n    \"programName\": \"신입사원 온보딩\",\n    \"category\": \"BASIC_TRAINING\",\n    \"trainingHour\": 4,\n    \"isOnline\": true,\n    \"startDate\": \"2024-02-01\",\n    \"capacity\": 15,\n    \"status\": \"RECRUITING\"\n  }\n}"))
            )
        }
    )
    public ResponseEntity<ApiResponse<Object>> createProgram(
        @Valid @RequestBody ProgramCreateRequestDto requestDto
    ) {
        // 요청 데이터 로깅
        System.out.println("교육 프로그램 추가 요청: " + requestDto);

        // Mock 데이터 생성
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("programId", 2001L);
        data.put("programName", requestDto.getProgramName());
        data.put("category", requestDto.getCategory());
        data.put("trainingHour", requestDto.getTrainingHour());
        data.put("isOnline", requestDto.getIsOnline());
        data.put("startDate", requestDto.getStartDate());
        data.put("capacity", requestDto.getCapacity());
        data.put("requiredDepartments", requestDto.getRequiredDepartments());
        data.put("requiredPositions", requestDto.getRequiredPositions());
        data.put("description", requestDto.getDescription());
        data.put("status", "RECRUITING");

        return ResponseEntity.ok(ApiResponse.success(
            data, "교육 프로그램이 추가되었습니다.", HttpStatus.OK
        ));
    }

    // ==================== 직원 교육 현황 조회 ====================
    // GET /api/business/hrm/training-status?department=&position=&name=&page=&size=
    @GetMapping("/training-status")
    @Operation(
        summary = "직원 교육 현황 목록 조회",
        description = "부서/직급/이름으로 직원 교육 현황 목록을 조회합니다.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "성공",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"목록 조회에 성공했습니다.\",\n  \"data\": {\n    \"items\": [{\n      \"employeeId\": 101,\n      \"name\": \"김민수\",\n      \"department\": \"개발팀\",\n      \"position\": \"과장\",\n      \"completedCount\": 8,\n      \"inProgressCount\": 2,\n      \"requiredMissingCount\": 3,\n      \"lastTrainingDate\": \"2024-01-10\"\n    }],\n    \"page\": { \"number\": 0, \"size\": 20, \"totalElements\": 4, \"totalPages\": 1 }\n  }\n}"))
            )
        }
    )
    public ResponseEntity<ApiResponse<Object>> getTrainingStatusList(
        @Parameter(description = "부서 ID")
        @RequestParam(name = "department", required = false) Long departmentId,
        @Parameter(description = "직급 ID")
        @RequestParam(name = "position", required = false) Long positionId,
        @Parameter(description = "직원 이름")
        @RequestParam(name = "name", required = false) String employeeName,
        @Parameter(description = "페이지 번호(0-base)")
        @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
        @Parameter(description = "페이지 크기(최대 200)")
        @RequestParam(name = "size", required = false, defaultValue = "20") Integer size
    ) {
        List<Map<String, String>> errors = new ArrayList<>();
        if (page != null && page < 0) {
            errors.add(Map.of("field", "page", "reason", "MIN_0"));
        }
        if (size != null && size > 200) {
            errors.add(Map.of("field", "size", "reason", "MAX_200"));
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(ErrorCode.VALIDATION_FAILED, errors);
        }

        int p = page == null ? 0 : page;
        int s = (size == null || size < 1) ? 20 : size;

        Map<String, Object> data = generateTrainingStatusPageMock(p, s);
        return ResponseEntity.ok(ApiResponse.success(
            data, "목록 조회에 성공했습니다.", HttpStatus.OK
        ));
    }

    // 직원 교육 현황 상세 조회
    // GET /api/business/training/employee/{employeeId}
    @GetMapping("/employee/{employeeId}")
    @Operation(
        summary = "직원 교육 현황 상세 조회",
        description = "특정 직원의 교육 현황 및 이력을 조회합니다.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "성공",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"직원 교육 이력 조회에 성공했습니다.\",\n  \"data\": {\n    \"employeeId\": \"EMP-001\",\n    \"employeeName\": \"김민수\",\n    \"department\": \"개발팀\",\n    \"position\": \"과장\",\n    \"completedCount\": 5,\n    \"inProgressCount\": 2,\n    \"requiredMissingCount\": 1,\n    \"lastTrainingDate\": \"2024-08-15\",\n    \"programHistory\": [{\n      \"programId\": 101,\n      \"programName\": \"React 기초\",\n      \"programStatus\": \"완료\",\n      \"date\": \"2024-01-10\"\n    }]\n  }\n}"))
            )
        }
    )
    public ResponseEntity<ApiResponse<Object>> getTrainingStatusDetail(
        @Parameter(description = "직원 ID", example = "101")
        @PathVariable("employeeId") Long employeeId
    ) {
        if (employeeId == null || employeeId < 1) {
            throw new ValidationException(ErrorCode.VALIDATION_FAILED,
                List.of(Map.of("field", "employeeId", "reason", "MIN_1")));
        }

        Map<String, Object> data = generateTrainingStatusDetailMock(employeeId);
        return ResponseEntity.ok(ApiResponse.success(
            data, "직원 교육 이력 조회에 성공했습니다.", HttpStatus.OK
        ));
    }

    // 교육 프로그램 목록 조회
    // GET /api/business/training/program?name=&status=&category=&page=&size=
    @GetMapping("/program")
    @Operation(
        summary = "교육 프로그램 목록 조회",
        description = "프로그램 이름/상태/카테고리로 교육 프로그램 목록을 조회합니다.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "성공",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"교육 프로그램 목록 조회에 성공했습니다.\",\n  \"data\": {\n    \"content\": [{\n      \"programId\": 1,\n      \"programName\": \"신입사원 온보딩\",\n      \"status\": \"IN_PROGRESS\",\n      \"category\": \"BASIC_TRAINING\",\n      \"trainingHour\": 4,\n      \"isOnline\": true,\n      \"capacity\": 15\n    }],\n    \"totalPages\": 5,\n    \"totalElements\": 48\n  }\n}"))
            )
        }
    )
    public ResponseEntity<ApiResponse<Object>> getTrainingPrograms(
        @Parameter(description = "프로그램 이름")
        @RequestParam(name = "name", required = false) String programName,
        @Parameter(description = "상태: IN_PROGRESS, COMPLETED, RECRUITING")
        @RequestParam(name = "status", required = false) String status,
        @Parameter(description = "카테고리: BASIC_TRAINING, TECHNICAL_TRAINING, SOFT_SKILL_TRAINING, MARKETING_TRAINING")
        @RequestParam(name = "category", required = false) String category,
        @Parameter(description = "페이지 번호(0-base)")
        @RequestParam(name = "page", required = false) Integer page,
        @Parameter(description = "페이지 크기(최대 200)")
        @RequestParam(name = "size", required = false) Integer size
    ) {
        List<Map<String, String>> errors = new ArrayList<>();
        if (status != null) {
            var allowedStatus = Set.of("IN_PROGRESS", "COMPLETED", "RECRUITING");
            if (!allowedStatus.contains(status)) {
                errors.add(Map.of("field", "status", "reason",
                    "ALLOWED_VALUES: IN_PROGRESS, COMPLETED, RECRUITING"));
            }
        }
        if (category != null) {
            var allowedCategory = Set.of("BASIC_TRAINING", "TECHNICAL_TRAINING",
                "SOFT_SKILL_TRAINING", "MARKETING_TRAINING");
            if (!allowedCategory.contains(category)) {
                errors.add(Map.of("field", "category", "reason",
                    "ALLOWED_VALUES: BASIC_TRAINING, TECHNICAL_TRAINING, SOFT_SKILL_TRAINING, MARKETING_TRAINING"));
            }
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

        Map<String, Object> data = generateTrainingProgramPageMock(p, s);
        return ResponseEntity.ok(ApiResponse.success(
            data, "교육 프로그램 목록 조회에 성공했습니다.", HttpStatus.OK
        ));
    }

    // 교육 프로그램 상세 조회
    // GET /api/business/training/program/{programId}
    @GetMapping("/program/{programId}")
    @Operation(
        summary = "교육 프로그램 상세 조회",
        description = "단일 교육 프로그램 상세 정보를 조회합니다.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "성공",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"교육 프로그램 상세 정보 조회에 성공했습니다.\",\n  \"data\": {\n    \"programId\": 1,\n    \"programName\": \"신입사원 온보딩\",\n    \"category\": \"BASIC_TRAINING\",\n    \"trainingHour\": 4,\n    \"isOnline\": true,\n    \"startDate\": \"2025-10-07\",\n    \"status\": \"IN_PROGRESS\",\n    \"designatedEmployee\": [{\n      \"employeeId\": 1,\n      \"employeeName\": \"김신입\",\n      \"department\": \"개발팀\",\n      \"position\": \"사원\",\n      \"status\": \"INCOMPLETED\",\n      \"completedAt\": null\n    }]}\n  }\n}"))
            )
        }
    )
    public ResponseEntity<ApiResponse<Object>> getTrainingProgramDetail(
        @Parameter(description = "프로그램 ID", example = "1")
        @PathVariable("programId") Long programId
    ) {
        if (programId == null || programId < 1) {
            throw new ValidationException(ErrorCode.VALIDATION_FAILED,
                List.of(Map.of("field", "programId", "reason", "MIN_1")));
        }

        Map<String, Object> data = generateTrainingProgramDetailMock(programId);
        return ResponseEntity.ok(ApiResponse.success(
            data, "교육 프로그램 상세 정보 조회에 성공했습니다.", HttpStatus.OK
        ));
    }

    // ==================== Mock 생성 함수 ====================

    private Map<String, Object> generateTrainingStatusPageMock(int page, int size) {
        List<Map<String, Object>> items = new ArrayList<>();
        items.add(buildStatusRow(101L, "김민수", "개발팀", "과장", 8, 2, 3, LocalDate.of(2024, 1, 10)));
        items.add(buildStatusRow(102L, "이영희", "마케팅팀", "사원", 12, 1, 2, LocalDate.of(2024, 1, 8)));
        items.add(buildStatusRow(103L, "박철수", "영업팀", "대리", 6, 3, 4, LocalDate.of(2024, 1, 5)));
        items.add(buildStatusRow(104L, "정수진", "인사팀", "팀장", 15, 1, 1, LocalDate.of(2024, 1, 12)));

        int totalElements = items.size();
        int totalPages = size <= 0 ? 0 : (int) Math.ceil((double) totalElements / size);
        PageDto pageInfo = PageDto.builder()
            .number(page)
            .size(size)
            .totalElements(totalElements)
            .totalPages(totalPages)
            .hasNext(page + 1 < totalPages)
            .build();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("items", items);
        response.put("page", pageInfo);
        return response;
    }

    private Map<String, Object> buildStatusRow(
        Long employeeId,
        String name,
        String department,
        String position,
        int completedCount,
        int inProgressCount,
        int requiredMissingCount,
        LocalDate lastTrainingDate
    ) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("employeeId", employeeId);
        row.put("name", name);
        row.put("department", department);
        row.put("position", position);
        row.put("completedCount", completedCount);
        row.put("inProgressCount", inProgressCount);
        row.put("requiredMissingCount", requiredMissingCount);
        row.put("lastTrainingDate", lastTrainingDate.atStartOfDay());
        return row;
    }

    private Map<String, Object> generateTrainingStatusDetailMock(Long employeeId) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("employeeId", employeeId);
        data.put("employeeName", "김민수");
        data.put("department", "개발팀");
        data.put("position", "과장");
        data.put("completedCount", 5);
        data.put("inProgressCount", 2);
        data.put("requiredMissingCount", 1);
        data.put("lastTrainingDate", LocalDate.of(2024, 8, 15).atStartOfDay());

        List<Map<String, Object>> history = new ArrayList<>();
        history.add(buildProgramHistory(101L, "React 기초", "완료", LocalDate.of(2024, 1, 10)));
        history.add(buildProgramHistory(105L, "효과적인 커뮤니케이션", "완료", LocalDate.of(2024, 3, 22)));
        history.add(buildProgramHistory(110L, "Java Spring 심화", "진행중", LocalDate.of(2024, 9, 1)));
        history.add(buildProgramHistory(112L, "프로젝트 관리 방법론", "진행중", LocalDate.of(2024, 9, 15)));
        data.put("programHistory", history);
        return data;
    }

    private Map<String, Object> buildProgramHistory(Long programId, String name, String status,
        LocalDate date) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("programId", programId);
        row.put("programName", name);
        row.put("programStatus", status);
        row.put("date", date);
        return row;
    }

    private Map<String, Object> generateTrainingProgramPageMock(int page, int size) {
        List<Map<String, Object>> content = new ArrayList<>();
        content.add(buildProgramRow(1L, "신입사원 온보딩", "IN_PROGRESS", "BASIC_TRAINING", 4, true, 15));
        content.add(
            buildProgramRow(2L, "React 심화 과정", "IN_PROGRESS", "TECHNICAL_TRAINING", 20, true, 25));
        content.add(
            buildProgramRow(3L, "중간 관리자 리더십 교육", "RECRUITING", "SOFT_SKILL_TRAINING", 16, false,
                20));
        content.add(
            buildProgramRow(4L, "디지털 마케팅 실무", "COMPLETED", "MARKETING_TRAINING", 8, true, 30));

        int totalElements = 48;
        int totalPages = size <= 0 ? 0 : (int) Math.ceil((double) totalElements / size);
        PageDto pageInfo = PageDto.builder()
            .number(page)
            .size(size)
            .totalElements(totalElements)
            .totalPages(totalPages)
            .hasNext(page + 1 < totalPages)
            .build();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("content", content);
        response.put("page", pageInfo);
        response.put("first", page == 0);
        response.put("last", page + 1 >= totalPages);
        response.put("numberOfElements", content.size());
        response.put("empty", content.isEmpty());
        return response;
    }

    private Map<String, Object> buildProgramRow(
        Long programId, String programName, String status, String category, int trainingHour,
        boolean isOnline, int capacity
    ) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("programId", programId);
        row.put("programName", programName);
        row.put("status", status);
        row.put("category", category);
        row.put("trainingHour", trainingHour);
        row.put("isOnline", isOnline);
        row.put("capacity", capacity);
        return row;
    }

    private Map<String, Object> generateTrainingProgramDetailMock(Long programId) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("programId", programId);
        data.put("programName", "신입사원 온보딩");
        data.put("programDescription", "신입사원을 위한 기본 교육 프로그램입니다.");
        data.put("category", "BASIC_TRAINING");
        data.put("trainingHour", 4);
        data.put("isOnline", true);
        data.put("startDate", LocalDate.of(2025, 10, 7));
        data.put("status", "IN_PROGRESS");

        List<Map<String, Object>> designated = new ArrayList<>();
        designated.add(buildDesignatedEmployee(1L, "김신입", "개발팀", "사원", "INCOMPLETED", null));
        designated.add(buildDesignatedEmployee(10L, "이초롱", "마케팅팀", "사원", "COMPLETED",
            LocalDate.of(2025, 10, 14)));
        designated.add(buildDesignatedEmployee(12L, "박새싹", "기획팀", "사원", "INCOMPLETED", null));
        data.put("designatedEmployee", designated);
        return data;
    }

    private Map<String, Object> buildDesignatedEmployee(
        Long employeeId,
        String employeeName,
        String department,
        String position,
        String status,
        LocalDate completedAt
    ) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("employeeId", employeeId);
        row.put("employeeName", employeeName);
        row.put("department", department);
        row.put("position", position);
        row.put("status", status);
        row.put("completedAt", completedAt);
        return row;
    }

}

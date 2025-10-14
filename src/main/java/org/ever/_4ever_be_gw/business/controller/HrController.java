package org.ever._4ever_be_gw.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.ever._4ever_be_gw.business.dto.EmployeeCreateRequestDto;
import org.ever._4ever_be_gw.business.dto.EmployeeUpdateRequestDto;
import org.ever._4ever_be_gw.common.exception.BusinessException;
import org.ever._4ever_be_gw.common.exception.ErrorCode;
import org.ever._4ever_be_gw.common.exception.ValidationException;
import org.ever._4ever_be_gw.common.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/business/hr")
@Tag(name = "HR Management", description = "인사 관리 API")
public class HrController {

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
                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"직원 목록을 조회했습니다.\",\n  \"data\": {\n    \"content\": [\n      {\n        \"employeeId\": 1,\n        \"employeeNumber\": \"EMP001\",\n        \"name\": \"김철수\",\n        \"email\": \"kim@company.com\",\n        \"phone\": \"010-1234-5678\",\n        \"position\": \"대리\",\n        \"department\": \"개발팀\",\n        \"status\": \"ACTIVE\",\n        \"statusLabel\": \"재직\",\n        \"hireDate\": \"2023-01-15\",\n        \"birthDate\": \"1990-05-20\",\n        \"address\": \"서울시 강남구\",\n        \"emergencyContact\": \"김영희\",\n        \"emergencyPhone\": \"010-9876-5432\",\n        \"bankAccount\": \"1234567890\",\n        \"bankName\": \"국민은행\",\n        \"accountHolder\": \"김철수\",\n        \"createdAt\": \"2023-01-15T09:00:00Z\",\n        \"updatedAt\": \"2024-01-15T09:00:00Z\",\n        \"skills\": [\"Java\", \"Spring Boot\", \"React\"],\n        \"managerName\": \"박영수\",\n        \"managerId\": 2,\n        \"workLocation\": \"본사\",\n        \"employmentType\": \"정규직\"\n      }\n    ],\n    \"page\": {\n      \"number\": 0,\n      \"size\": 20,\n      \"totalElements\": 150,\n      \"totalPages\": 8,\n      \"hasNext\": true\n    }\n  }\n}"))
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
        int p = page == null ? 0 : page;
        int s = (size == null || size < 1) ? 20 : size;

        // Mock 데이터 생성
        List<Map<String, Object>> content = generateEmployeeListMockData(10);

        Map<String, Object> pageMeta = new LinkedHashMap<>();
        pageMeta.put("number", p);
        pageMeta.put("size", s);
        pageMeta.put("totalElements", 150);
        pageMeta.put("totalPages", 8);
        pageMeta.put("hasNext", (p + 1) < 8);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("content", content);
        data.put("page", pageMeta);

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
                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"직원 상세 정보를 조회했습니다.\",\n  \"data\": {\n    \"employeeId\": 1,\n    \"employeeNumber\": \"EMP001\",\n    \"name\": \"김철수\",\n    \"email\": \"kim@company.com\",\n    \"phone\": \"010-1234-5678\",\n    \"position\": \"대리\",\n    \"department\": \"개발팀\",\n    \"status\": \"ACTIVE\",\n    \"statusLabel\": \"재직\",\n    \"hireDate\": \"2023-01-15\",\n    \"birthDate\": \"1990-05-20\",\n    \"address\": \"서울시 강남구 테헤란로 123\",\n    \"emergencyContact\": \"김영희\",\n    \"emergencyPhone\": \"010-9876-5432\",\n    \"bankAccount\": \"1234567890\",\n    \"bankName\": \"국민은행\",\n    \"accountHolder\": \"김철수\",\n    \"createdAt\": \"2023-01-15T09:00:00Z\",\n    \"updatedAt\": \"2024-01-15T09:00:00Z\",\n    \"skills\": [\"Java\", \"Spring Boot\", \"React\", \"MySQL\"],\n    \"managerName\": \"박영수\",\n    \"managerId\": 2,\n    \"workLocation\": \"본사\",\n    \"employmentType\": \"정규직\"\n  }\n}"))
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
                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"부서 목록을 조회했습니다.\",\n  \"data\": {\n    \"total\": 8,\n    \"page\": 1,\n    \"size\": 10,\n    \"totalPages\": 1,\n    \"hasNext\": false,\n    \"hasPrev\": false,\n    \"departments\": [\n      {\n        \"departmentId\": 1,\n        \"departmentCode\": \"DEV\",\n        \"departmentName\": \"개발팀\",\n        \"description\": \"소프트웨어 개발 및 유지보수\",\n        \"managerName\": \"박영수\",\n        \"managerId\": 2,\n        \"location\": \"본사 3층\",\n        \"status\": \"ACTIVE\",\n        \"statusLabel\": \"활성\",\n        \"employeeCount\": 25,\n        \"budget\": 500000000.0,\n        \"budgetCurrency\": \"KRW\",\n        \"establishedDate\": \"2020-01-01\",\n        \"createdAt\": \"2020-01-01T00:00:00Z\",\n        \"updatedAt\": \"2024-01-01T00:00:00Z\",\n        \"responsibilities\": [\"소프트웨어 개발\", \"시스템 유지보수\", \"기술 연구\"],\n        \"parentDepartment\": \"IT본부\",\n        \"parentDepartmentId\": 1\n      }\n    ]\n  }\n}"))
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

        // Mock 데이터 생성
        List<Map<String, Object>> departments = generateDepartmentListMockData();

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("total", departments.size());
        data.put("page", page);
        data.put("size", size);
        data.put("totalPages", 1);
        data.put("hasNext", false);
        data.put("hasPrev", false);
        data.put("departments", departments);

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
                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"출퇴근 기록을 조회했습니다.\",\n  \"data\": {\n    \"content\": [\n      {\n        \"attendanceId\": 1,\n        \"employeeId\": 1,\n        \"employeeName\": \"김철수\",\n        \"employeeNumber\": \"EMP001\",\n        \"attendanceDate\": \"2024-01-15\",\n        \"checkInTime\": \"09:00:00\",\n        \"checkOutTime\": \"18:00:00\",\n        \"status\": \"NORMAL\",\n        \"statusLabel\": \"정상\",\n        \"workType\": \"OFFICE\",\n        \"workTypeLabel\": \"사무실\",\n        \"location\": \"본사\",\n        \"notes\": \"\",\n        \"workingHours\": 8.0,\n        \"overtimeHours\": 0.0,\n        \"approvalStatus\": \"APPROVED\",\n        \"approvalStatusLabel\": \"승인\",\n        \"approverName\": \"박영수\",\n        \"approverId\": 2,\n        \"createdAt\": \"2024-01-15T09:00:00Z\",\n        \"updatedAt\": \"2024-01-15T18:00:00Z\",\n        \"department\": \"개발팀\",\n        \"position\": \"대리\",\n        \"isLate\": false,\n        \"isEarlyLeave\": false,\n        \"lateReason\": null,\n        \"earlyLeaveReason\": null\n      }\n    ],\n    \"page\": {\n      \"number\": 0,\n      \"size\": 20,\n      \"totalElements\": 500,\n      \"totalPages\": 25,\n      \"hasNext\": true\n    }\n  }\n}"))
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

        Map<String, Object> pageMeta = new LinkedHashMap<>();
        pageMeta.put("number", p);
        pageMeta.put("size", s);
        pageMeta.put("totalElements", 500);
        pageMeta.put("totalPages", 25);
        pageMeta.put("hasNext", (p + 1) < 25);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("content", content);
        data.put("page", pageMeta);

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
                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"휴가 신청 목록을 조회했습니다.\",\n  \"data\": {\n    \"content\": [\n      {\n        \"leaveRequestId\": 1,\n        \"employeeId\": 1,\n        \"employeeName\": \"김철수\",\n        \"employeeNumber\": \"EMP001\",\n        \"leaveType\": \"ANNUAL\",\n        \"leaveTypeLabel\": \"연차\",\n        \"reason\": \"개인사정\",\n        \"description\": \"가족 행사 참석\",\n        \"startDate\": \"2024-01-20\",\n        \"endDate\": \"2024-01-22\",\n        \"totalDays\": 3.0,\n        \"status\": \"PENDING\",\n        \"statusLabel\": \"대기\",\n        \"approvalStatus\": \"PENDING\",\n        \"approvalStatusLabel\": \"승인대기\",\n        \"approverName\": \"박영수\",\n        \"approverId\": 2,\n        \"approverComment\": null,\n        \"requestDate\": \"2024-01-15T09:00:00Z\",\n        \"approvalDate\": null,\n        \"createdAt\": \"2024-01-15T09:00:00Z\",\n        \"updatedAt\": \"2024-01-15T09:00:00Z\",\n        \"department\": \"개발팀\",\n        \"position\": \"대리\",\n        \"emergencyContact\": \"김영희\",\n        \"emergencyPhone\": \"010-9876-5432\",\n        \"isPaidLeave\": true,\n        \"remainingLeaveDays\": 12.0,\n        \"attachmentUrl\": null,\n        \"attachments\": []\n      }\n    ],\n    \"page\": {\n      \"number\": 0,\n      \"size\": 20,\n      \"totalElements\": 80,\n      \"totalPages\": 4,\n      \"hasNext\": true\n    }\n  }\n}"))
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

        Map<String, Object> pageMeta = new LinkedHashMap<>();
        pageMeta.put("number", p);
        pageMeta.put("size", s);
        pageMeta.put("totalElements", 80);
        pageMeta.put("totalPages", 4);
        pageMeta.put("hasNext", (p + 1) < 4);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("content", content);
        data.put("page", pageMeta);

        return ResponseEntity.ok(ApiResponse.success(
            data, "휴가 신청 목록을 조회했습니다.", HttpStatus.OK
        ));
    }

    // ==================== Mock 데이터 생성 메서드들 ====================

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
        dept.put("establishedDate", LocalDateTime.now());
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
            record.put("createdAt", LocalDateTime.now().minusDays(i));
            record.put("updatedAt", LocalDateTime.now().minusDays(i));
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
            request.put("requestDate", LocalDateTime.now().minusDays(i));
            request.put("approvalDate", i % 3 == 1 ? LocalDateTime.now().minusDays(i - 1) : null);
            request.put("createdAt", LocalDateTime.now().minusDays(i));
            request.put("updatedAt", LocalDateTime.now().minusDays(i));
            request.put("department", "개발팀");
            request.put("position", "대리");
            requests.add(request);
        }
        return requests;
    }

}

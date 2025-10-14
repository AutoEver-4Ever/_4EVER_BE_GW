package org.ever._4ever_be_gw.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.ever._4ever_be_gw.business.dto.LeaveRequestDto;
import org.ever._4ever_be_gw.business.dto.TimeRecordUpdateRequestDto;
import org.ever._4ever_be_gw.common.exception.ErrorCode;
import org.ever._4ever_be_gw.common.exception.ValidationException;
import org.ever._4ever_be_gw.common.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/business/tam")
@Tag(name = "Time & Attendance Management", description = "근태 관리 API")
public class TamController {

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
                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"휴가 신청이 승인되었습니다.\",\n  \"data\": {\n    \"leaveRequestId\": 201,\n    \"status\": \"APPROVED\",\n    \"approvedAt\": \"2025-01-15T10:30:00\"\n  }\n}"))
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
        data.put("approvedAt", LocalDateTime.now());

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
                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"휴가 신청이 반려되었습니다.\",\n  \"data\": {\n    \"leaveRequestId\": 201,\n    \"status\": \"REJECTED\",\n    \"rejectedAt\": \"2025-01-15T10:30:00\"\n  }\n}"))
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
        data.put("rejectedAt", LocalDateTime.now());

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
        response.put("totalPages", 4);
        response.put("totalElements", 35);
        response.put("last", page >= 3);
        response.put("size", size);
        response.put("number", page);
        response.put("sort", sort);
        response.put("numberOfElements", content.size());
        response.put("first", page == 0);
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

        Map<String, Object> pageable = new LinkedHashMap<>();
        Map<String, Object> sort = new LinkedHashMap<>();
        sort.put("sorted", true);
        sort.put("unsorted", false);
        sort.put("empty", false);
        pageable.put("sort", sort);
        pageable.put("offset", page * size);
        pageable.put("pageNumber", page);
        pageable.put("pageSize", size);
        pageable.put("paged", true);
        pageable.put("unpaged", false);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("content", content);
        response.put("pageable", pageable);
        response.put("totalPages", 5);
        response.put("totalElements", 98);
        response.put("last", page >= 4);
        response.put("size", size);
        response.put("number", page);
        response.put("sort", sort);
        response.put("numberOfElements", content.size());
        response.put("first", page == 0);
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
}



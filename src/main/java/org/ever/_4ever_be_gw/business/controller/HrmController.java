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
import java.util.concurrent.ThreadLocalRandom;

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
        description = "기간별 인적자원 통계 정보를 조회합니다."
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
        description = "새로운 직원을 등록합니다."
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
        @Parameter(description = "직원 ID", example = "0193e7c8-1234-7abc-9def-0123456789ab")
        @PathVariable("employeeId") String employeeId,
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
        description = "직원 목록을 페이지네이션으로 조회합니다."
    )
    public ResponseEntity<ApiResponse<Object>> getEmployees(
        @Parameter(description = "부서 필터")
        @RequestParam(name = "department", required = false) String department,
        @Parameter(description = "직급 필터")
        @RequestParam(name = "position", required = false) String position,
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
        description = "직원 상세 정보를 조회합니다."
    )
    public ResponseEntity<ApiResponse<Object>> getEmployeeDetail(
        @Parameter(description = "직원 ID", example = "0193e7c8-1234-7abc-9def-0123456789ab")
        @PathVariable("employeeId") String employeeId
    ) {
        if (employeeId == null || employeeId.isBlank()) {
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
        description = "부서 목록을 조회합니다."
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
        description = "직급 목록을 조회합니다."
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
        description = "직급 상세 정보를 조회합니다."
    )
    public ResponseEntity<ApiResponse<Object>> getPositionDetail(
        @Parameter(description = "직급 ID", example = "0193e7c8-1234-7abc-9def-0123456789ab")
        @PathVariable("positionId") String positionId
    ) {
        if (positionId == null || positionId.isBlank()) {
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
        description = "출퇴근 기록을 조회합니다."
    )
    public ResponseEntity<ApiResponse<Object>> getAttendance(
        @Parameter(description = "직원 ID", example = "0193e7c8-1234-7abc-9def-0123456789ab")
        @RequestParam(name = "employeeId", required = false) String employeeId,
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
            .put("totalEmployeeCount", PeriodStatDto.builder()
                .value(totalEmployeeCount)
                .deltaRate(BigDecimal.valueOf(totalEmployeeDelta))
                .build())
            .put("newEmployeeCount", PeriodStatDto.builder()
                .value(newEmployeeCount)
                .deltaRate(BigDecimal.valueOf(newEmployeeDelta))
                .build())
            .put("ongoingProgramCount", PeriodStatDto.builder()
                .value(ongoingProgramCount)
                .deltaRate(BigDecimal.valueOf(ongoingProgramDelta))
                .build())
            .put("completedProgramCount", PeriodStatDto.builder()
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
            employee.put("employeeId", uuidV7());
            employee.put("employeeCode", String.format("EMP%03d", i + 1));
            employee.put("employeeName", names[i % names.length]);

            employee.put("positionId", uuidV7());
            employee.put("position", positions[i % positions.length]);
            employee.put("departmentId", uuidV7());
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
            employee.put("employeeId", uuidV7());
            employee.put("employeeNumber", String.format("EMP%03d", i + 1));
            employee.put("name", names[i % names.length]);
            employee.put("email", String.format("employee%d@company.com", i + 1));
            employee.put("phone", String.format("010-%04d-%04d", 1000 + i, 1000 + i));
            employee.put("position", positions[i % positions.length]);
            employee.put("department", departments[i % departments.length]);
            employee.put("statusCode", "ACTIVE");
            employee.put("hireDate", LocalDate.now().minusMonths(12 + i));
            employee.put("birthDate", LocalDate.of(1990 + (i % 10), (i % 12) + 1, (i % 28) + 1));
            employee.put("address", "서울시 강남구 테헤란로 " + (100 + i));
            employee.put("createdAt", LocalDate.now().minusMonths(12 + i));
            employee.put("updatedAt", LocalDate.now());

            employees.add(employee);
        }
        return employees;
    }

    private Map<String, Object> generateEmployeeDetailMockData(String employeeId) {
        Map<String, Object> employee = new LinkedHashMap<>();
        employee.put("employeeId", uuidV7());
        employee.put("employeeNumber", "EMP-001");
        employee.put("name", "김철수");
        employee.put("email", "kim@company.com");
        employee.put("phone", "010-1234-5678");
        employee.put("position", "대리");
        employee.put("department", "개발팀");
        employee.put("statusCode", "ACTIVE");
        employee.put("hireDate", LocalDate.of(2023, 1, 15));
        employee.put("birthDate", LocalDate.of(1990, 5, 20));
        employee.put("address", "서울시 강남구 테헤란로 123");
        employee.put("academicHistory", "서울대학교 컴퓨터공학과 졸업");
        employee.put("careerHistory", "5년차 소프트웨어 개발자");
        employee.put("createdAt", LocalDate.of(2023, 1, 15));
        employee.put("updatedAt", LocalDate.of(2024, 1, 15));

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
        String[] codes = {"DEV", "PLAN", "MKT", "HR", "FIN", "SALES", "DESIGN", "QA"};
        String[] managers = {"박영수", "김영희", "이민수", "최수진", "정지훈", "한미영", "송동현", "강서연"};
        String[] locations = {"본사 3층", "본사 2층", "본사 4층", "본사 1층", "본사 5층", "지사 1층", "본사 3층", "본사 4층"};
        String[] descriptions = {
            "소프트웨어 개발 및 유지보수",
            "사업 기획 및 전략 수립",
            "마케팅 및 홍보",
            "인사 관리 및 복지",
            "재무 관리 및 회계",
            "영업 및 고객 관리",
            "UX/UI 디자인",
            "품질 관리 및 테스트"
        };

        for (int i = 0; i < names.length; i++) {
            Map<String, Object> dept = new LinkedHashMap<>();
            dept.put("departmentId", uuidV7());
            dept.put("departmentNumber", codes[i]);
            dept.put("departmentName", names[i]);
            dept.put("description", descriptions[i]);
            dept.put("managerName", managers[i]);
            dept.put("managerId", uuidV7());
            dept.put("location", locations[i]);
            dept.put("statusCode", "ACTIVE");
            dept.put("employeeCount", 20 + i * 5);
            dept.put("budget", 500000000.0 + i * 100000000.0);
            dept.put("budgetCurrency", "KRW");
            dept.put("establishedDate", LocalDate.of(2020, 1, 1));
            dept.put("createdAt", LocalDate.of(2020, 1, 1));
            dept.put("updatedAt", LocalDate.of(2024, 1, 1));
            dept.put("responsibilities", List.of("주요 업무 " + (i + 1), "보조 업무 " + (i + 1)));

            departments.add(dept);
        }
        return departments;
    }

    private Map<String, Object> generateDepartmentDetailMockData() {
        Map<String, Object> dept = new LinkedHashMap<>();
        dept.put("departmentId", 1);
        dept.put("departmentNumber", "DEV");
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
            position.put("positionId", uuidV7());
            position.put("positionName", positionNames[i]);
            position.put("headCount", headCounts[i]);
            position.put("payment", annualSalaries[i]);

            positions.add(position);
        }
        return positions;
    }

    private Map<String, Object> generatePositionDetailMockData(String positionId) {
        Map<String, Object> position = new LinkedHashMap<>();
        String[] positionNames = {"사원", "주임", "대리", "과장", "차장", "부장", "이사", "상무", "전무", "사장"};
        String[] positionNumbers = {"AA-001", "AA-002", "AA-003", "AA-004", "AA-005",
            "AA-006", "AA-007", "AA-008", "AA-009", "AA-010"};
        int[] headCounts = {15, 12, 10, 8, 6, 4, 3, 2, 1, 1};
        int[] annualSalaries = {30000000, 35000000, 40000000, 50000000, 60000000, 70000000,
            80000000, 90000000, 100000000, 120000000};

        int index = Math.abs(positionId.hashCode() % positionNames.length);

        position.put("positionId", uuidV7());
        position.put("positionNumber", positionNumbers[index]);
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

        for (int i = 0; i < count; i++) {
            Map<String, Object> record = new LinkedHashMap<>();
            record.put("attendanceId", uuidV7());
            record.put("employeeId", uuidV7());
            record.put("employeeName", names[i % names.length]);
            record.put("employeeNumber", String.format("EMP%03d", i + 1));
            record.put("attendanceDate", LocalDate.now().minusDays(i));
            record.put("checkInTime", "09:00:00");
            record.put("checkOutTime", "18:00:00");
            record.put("statusCode", statuses[i % statuses.length]);
            record.put("workType", "OFFICE");
            record.put("location", "본사");
            record.put("notes", "");
            record.put("workingHours", 8.0);
            record.put("overtimeHours", i % 2 == 0 ? 1.0 : 0.0);
            record.put("approvalStatus", "APPROVED");
            record.put("approverName", "박영수");
            record.put("approverId", uuidV7());
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
            request.put("approvalStatus", statuses[i % statuses.length]);
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
        description = "월별 사내 급여 명세서 목록을 조회합니다."
    )
    public ResponseEntity<ApiResponse<Object>> getMonthlyPayrollList(
        @Parameter(description = "연도", example = "2025")
        @RequestParam(name = "year") Integer year,
        @Parameter(description = "월(1~12)", example = "10")
        @RequestParam(name = "month") Integer month,
        @Parameter(description = "직원 이름(선택)")
        @RequestParam(name = "name", required = false) String employeeName,
        @Parameter(description = "부서 ID(선택)")
        @RequestParam(name = "department", required = false) String departmentId,
        @Parameter(description = "직급 ID(선택)")
        @RequestParam(name = "position", required = false) String positionId,
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

    // 급여 지급 완료 처리
    // POST /api/business/hrm/payroll/complete
    @PostMapping("/payroll/complete")
    @Operation(
        summary = "급여 지급 완료 처리",
        description = "급여 지급을 완료 처리합니다."
    )
    public ResponseEntity<ApiResponse<Object>> completePayroll(
        @Valid @RequestBody PayrollCompleteRequestDto requestDto
    ) {
        // 요청 데이터 로깅
        System.out.println("급여 지급 완료 처리 요청: " + requestDto);

        // Mock 데이터 생성
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("payrollId", requestDto.getPayrollId());
        data.put("statusCode", "COMPLETED");
        data.put("completedAt", LocalDateTime.now());

        return ResponseEntity.ok(ApiResponse.success(
            data, "급여 지급 완료 처리되었습니다.", HttpStatus.OK
        ));
    }

    // 월별 사내 급여 상세 조회
    // GET /api/business/hrm/payroll/{payrollId}
    @GetMapping("/payroll/{payrollId}")
    @Operation(
        summary = "월별 급여 상세 조회",
        description = "월별 사내 급여 명세서 상세를 조회합니다.",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "성공",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"급여 명세서 상세 조회에 성공했습니다.\",\n  \"data\": {\n    \"payrollId\": \"0193e7c8-1234-7abc-9def-0123456789ab\",\n    \"employee\": {\n      \"employeeId\": \"0193e7c8-5678-7abc-9def-fedcba987654\",\n      \"employeeNumber\": \"EMP001\",\n      \"employeeName\": \"김민수\",\n      \"department\": \"개발팀\",\n      \"position\": \"과장\"\n    },\n    \"pay\": {\n      \"basePay\": 4500000,\n      \"basePayItem\": [{\n        \"itemContent\": \"정기 급여\",\n        \"itemSum\": 4200000\n      },{\n        \"itemContent\": \"직책 수당\",\n        \"itemSum\": 300000\n      }],\n      \"overtimePay\": 150000,\n      \"overtimePayItem\": [{\n        \"itemContent\": \"야간 근무 수당 (5시간)\",\n        \"itemSum\": 100000\n      },{\n        \"itemContent\": \"휴일 근무 수당 (2시간)\",\n        \"itemSum\": 50000\n      }],\n      \"deduction\": -450000,\n      \"deductionItem\": [{\n        \"itemContent\": \"국민연금\",\n        \"itemSum\": -200000\n      },{\n        \"itemContent\": \"건강보험\",\n        \"itemSum\": -150000\n      },{\n        \"itemContent\": \"소득세\",\n        \"itemSum\": -100000\n      }],\n      \"netPay\": 4200000\n    },\n    \"statusCode\": \"COMPLETED\",\n    \"expectedDate\": \"2024-01-25\"\n  }\n}"))
            )
        }
    )
    public ResponseEntity<ApiResponse<Object>> getMonthlyPayrollDetail(
        @Parameter(description = "급여 명세서 ID", example = "0193e7c8-1234-7abc-9def-0123456789ab")
        @PathVariable("payrollId") String payrollId
    ) {
        List<Map<String, String>> errors = new ArrayList<>();
        if (payrollId == null || payrollId.isBlank()) {
            errors.add(Map.of("field", "payrollId", "reason", "REQUIRED"));
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(ErrorCode.VALIDATION_FAILED, errors);
        }

        Map<String, Object> data = generateMonthlyPayrollDetailMock(payrollId);
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
            row.put("payrollId", uuidV7());

            Map<String, Object> employee = new LinkedHashMap<>();
            employee.put("employeeId", uuidV7());
            employee.put("employeeName", names[idx]);
            employee.put("departmentId", uuidV7());
            employee.put("department", departments[idx % departments.length]);
            employee.put("positionId", uuidV7());
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
            pay.put("statusCode", (i % 2 == 0 ? "PENDING" : "COMPLETED"));
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
        return response;
    }

    private Map<String, Object> generateMonthlyPayrollDetailMock(String paystubId) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("payrollId", uuidV7());

        Map<String, Object> employee = new LinkedHashMap<>();
        employee.put("employeeId", uuidV7());
        employee.put("employeeNumber", "EMP001");
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

        data.put("statusCode", "COMPLETED");
        data.put("expectedDate", LocalDate.now().withDayOfMonth(25));

        return data;
    }

    // ==================== 출퇴근 관리 ====================

    @PatchMapping("/attendance/check-in")
    @Operation(
        summary = "출근 상태 변경",
        description = "직원의 출근 상태를 변경합니다."
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
        description = "직원의 퇴근 상태를 변경합니다."
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
        description = "출퇴근 기록을 수정합니다."
    )
    public ResponseEntity<ApiResponse<Object>> updateTimeRecord(
        @Parameter(description = "근태 기록 ID", example = "0193e7c8-1234-7abc-9def-0123456789ab")
        @PathVariable("timerecordId") String timerecordId,
        @Valid @RequestBody TimeRecordUpdateRequestDto requestDto
    ) {
        // 요청 데이터 로깅
        System.out.println("출퇴근 기록 수정 요청 - ID: " + timerecordId + ", 데이터: " + requestDto);

        // Mock 데이터 생성
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("timerecordId", timerecordId);
        data.put("employeeId", 1L);
        data.put("checkInTime", requestDto.getCheckInTime());
        data.put("checkOutTime", requestDto.getCheckOutTime());
        data.put("statusCode", requestDto.getStatusCode());

        return ResponseEntity.ok(ApiResponse.success(
            data, "출퇴근 기록 수정이 완료되었습니다.", HttpStatus.OK
        ));
    }

    // ==================== 휴가 관리 ====================

    @PostMapping("/leave/request")
    @Operation(
        summary = "휴가 신청",
        description = "새로운 휴가를 신청합니다."
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
        description = "휴가 신청을 승인합니다."
    )
    public ResponseEntity<ApiResponse<Object>> approveLeaveRequest(
        @Parameter(description = "휴가 신청 ID", example = "0193e7c8-1234-7abc-9def-0123456789ab")
        @PathVariable("requestId") String requestId
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
        description = "휴가 신청을 반려합니다."
    )
    public ResponseEntity<ApiResponse<Object>> rejectLeaveRequest(
        @Parameter(description = "휴가 신청 ID", example = "0193e7c8-1234-7abc-9def-0123456789ab")
        @PathVariable("requestId") String requestId
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
        description = "부서/직책/이름/일자로 출퇴근 기록을 조회합니다."
    )
    public ResponseEntity<ApiResponse<Object>> getTimeRecords(
        @Parameter(description = "부서 ID")
        @RequestParam(name = "department", required = false) String departmentId,
        @Parameter(description = "직책 ID")
        @RequestParam(name = "position", required = false) String positionId,
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
        description = "단일 출퇴근 기록 상세 정보를 조회합니다."
    )
    public ResponseEntity<ApiResponse<Object>> getTimeRecordDetail(
        @Parameter(description = "출퇴근 기록 ID", example = "0193e7c8-1234-7abc-9def-0123456789ab")
        @PathVariable("timerecordId") String timerecordId
    ) {
        if (timerecordId == null || timerecordId.isBlank()) {
            throw new ValidationException(ErrorCode.VALIDATION_FAILED,
                List.of(Map.of("field", "timerecordId", "reason", "REQUIRED")));
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
        description = "부서/직책/이름/유형으로 휴가 신청 목록을 조회합니다."
    )
    public ResponseEntity<ApiResponse<Object>> getLeaveRequestList(
        @Parameter(description = "부서 ID")
        @RequestParam(name = "department", required = false) String departmentId,
        @Parameter(description = "직책 ID")
        @RequestParam(name = "position", required = false) String positionId,
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
        row.put("leaveRequestId", uuidV7());

        Map<String, Object> employee = new LinkedHashMap<>();
        employee.put("employeeId", uuidV7());
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
            row.put("timerecordId", uuidV7());

            Map<String, Object> employee = new LinkedHashMap<>();
            employee.put("employeeId", uuidV7());
            employee.put("employeeName", names[i]);
            employee.put("departmentId", uuidV7());
            employee.put("department", departments[i % departments.length]);
            employee.put("positionId", uuidV7());
            employee.put("position", positions[i % positions.length]);
            row.put("employee", employee);

            row.put("workDate", baseDate);
            row.put("checkInTime", LocalDateTime.of(baseDate.getYear(), baseDate.getMonthValue(),
                baseDate.getDayOfMonth(), 8 + i, (50 + i * 5) % 60, 10));
            row.put("checkOutTime", LocalDateTime.of(baseDate.getYear(), baseDate.getMonthValue(),
                baseDate.getDayOfMonth(), 18, (20 + i * 5) % 60, 35));
            row.put("totalWorkMinutes", totalMinutes[i]);
            row.put("overtimeMinutes", overtime[i]);
            row.put("statusCode", statuses[i]);

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

    private Map<String, Object> generateTimeRecordDetailMock(String timerecordId) {
        LocalDate baseDate = LocalDate.of(2025, 10, 7);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("timerecordId", uuidV7());

        Map<String, Object> employee = new LinkedHashMap<>();
        employee.put("employeeId", uuidV7());
        employee.put("employeeNumber", "EMP001");
        employee.put("employeeName", "김민수");
        employee.put("departmentId", uuidV7());
        employee.put("department", "개발팀");
        employee.put("positionId", uuidV7());
        employee.put("position", "과장");
        data.put("employee", employee);

        data.put("workDate", baseDate);
        data.put("checkInTime", LocalDateTime.of(2025, 10, 7, 8, 50, 10));
        data.put("checkOutTime", LocalDateTime.of(2025, 10, 7, 18, 20, 35));
        data.put("totalWorkMinutes", 510);
        data.put("overtimeMinutes", 60);
        data.put("statusCode", "ON_TIME");

        return data;
    }
    // ==================== 교육 신청 및 프로그램 관리 ====================

    @PostMapping("/employee/request")
    @Operation(
        summary = "교육 신청",
        description = "직원이 교육 프로그램에 신청합니다."
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

    @PostMapping("/program/{employeeId}")
    @Operation(
        summary = "직원에게 교육 프로그램 추가",
        description = "특정 직원에게 교육 프로그램을 할당합니다."
    )
    public ResponseEntity<ApiResponse<Object>> assignProgramToEmployee(
        @Parameter(description = "직원 ID", example = "0193e7c8-1234-7abc-9def-0123456789ab")
        @PathVariable("employeeId") String employeeId,
        @Valid @RequestBody ProgramAssignRequestDto requestDto
    ) {
        if (employeeId == null || employeeId.isBlank()) {
            throw new ValidationException(ErrorCode.VALIDATION_FAILED,
                List.of(Map.of("field", "employeeId", "reason", "REQUIRED")));
        }

        // 요청 데이터 로깅
        System.out.println("직원 교육 프로그램 추가 요청 - 직원 ID: " + employeeId + ", 데이터: " + requestDto);

        // Mock 데이터 생성
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("employeeId", employeeId);
        data.put("programId", requestDto.getProgramId());
        data.put("assignedAt", LocalDateTime.now());
        data.put("statusCode", "ASSIGNED");

        return ResponseEntity.ok(ApiResponse.success(
            data, "직원에게 교육 프로그램이 추가되었습니다.", HttpStatus.OK
        ));
    }

    @PostMapping("/program")
    @Operation(
        summary = "교육 프로그램 추가",
        description = "새로운 교육 프로그램을 추가합니다."
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

    @PatchMapping("/program/{programId}")
    @Operation(
        summary = "교육 프로그램 수정",
        description = "기존 교육 프로그램 정보를 수정합니다."
    )
    public ResponseEntity<ApiResponse<Object>> modifyProgram(
        @Parameter(description = "프로그램 ID", example = "0193e7c8-1234-7abc-9def-0123456789ab")
        @PathVariable("programId") String programId,
        @Valid @RequestBody ProgramModifyRequestDto requestDto
    ) {
        if (programId == null || programId.isBlank()) {
            throw new ValidationException(ErrorCode.VALIDATION_FAILED,
                List.of(Map.of("field", "programId", "reason", "REQUIRED")));
        }

        // 요청 데이터 로깅
        System.out.println("교육 프로그램 수정 요청 - ID: " + programId + ", 데이터: " + requestDto);

        // Mock 데이터 생성
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("programId", programId);
        data.put("programName", requestDto.getProgramName());
        data.put("statusCode", requestDto.getStatusCode());
        data.put("updatedAt", LocalDateTime.now());

        return ResponseEntity.ok(ApiResponse.success(
            data, "교육 프로그램이 수정되었습니다.", HttpStatus.OK
        ));
    }

    // ==================== 직원 교육 현황 조회 ====================
    // GET /api/business/hrm/training-status?department=&position=&name=&page=&size=
    @GetMapping("/training-status")
    @Operation(
        summary = "직원 교육 현황 목록 조회",
        description = "부서/직급/이름으로 직원 교육 현황 목록을 조회합니다."
    )
    public ResponseEntity<ApiResponse<Object>> getTrainingStatusList(
        @Parameter(description = "부서 ID")
        @RequestParam(name = "department", required = false) String departmentId,
        @Parameter(description = "직급 ID")
        @RequestParam(name = "position", required = false) String positionId,
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
    // GET /api/business/hrm/training/employee/{employeeId}
    @GetMapping("/training/employee/{employeeId}")
    @Operation(
        summary = "직원 교육 현황 상세 조회",
        description = "특정 직원의 교육 현황 및 이력을 조회합니다."
    )
    public ResponseEntity<ApiResponse<Object>> getTrainingStatusDetail(
        @Parameter(description = "직원 ID", example = "0193e7c8-1234-7abc-9def-0123456789ab")
        @PathVariable("employeeId") String employeeId
    ) {
        if (employeeId == null || employeeId.isBlank()) {
            throw new ValidationException(ErrorCode.VALIDATION_FAILED,
                List.of(Map.of("field", "employeeId", "reason", "REQUIRED")));
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
        description = "프로그램 이름/상태/카테고리로 교육 프로그램 목록을 조회합니다."
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
        description = "단일 교육 프로그램 상세 정보를 조회합니다."
    )
    public ResponseEntity<ApiResponse<Object>> getTrainingProgramDetail(
        @Parameter(description = "프로그램 ID", example = "0193e7c8-1234-7abc-9def-0123456789ab")
        @PathVariable("programId") String programId
    ) {
        if (programId == null || programId.isBlank()) {
            throw new ValidationException(ErrorCode.VALIDATION_FAILED,
                List.of(Map.of("field", "programId", "reason", "REQUIRED")));
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
        row.put("employeeId", uuidV7());
        row.put("name", name);
        row.put("department", department);
        row.put("position", position);
        row.put("completedCount", completedCount);
        row.put("inProgressCount", inProgressCount);
        row.put("requiredMissingCount", requiredMissingCount);
        row.put("lastTrainingDate", lastTrainingDate.atStartOfDay());
        return row;
    }

    private Map<String, Object> generateTrainingStatusDetailMock(String employeeId) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", uuidV7());
        data.put("employeeNumber", "EMP-001");
        data.put("employeeName", "김민수");
        data.put("department", "개발팀");
        data.put("position", "과장");
        data.put("completedCount", 5);
        data.put("inProgressCount", 2);
        data.put("requiredMissingCount", 1);
        data.put("lastTrainingDate", LocalDate.of(2024, 8, 15).atStartOfDay());

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
        row.put("programId", uuidV7());
        row.put("programName", programName);
        row.put("statusCode", status);
        row.put("category", category);
        row.put("trainingHour", trainingHour);
        row.put("isOnline", isOnline);
        row.put("capacity", capacity);
        return row;
    }

    private Map<String, Object> generateTrainingProgramDetailMock(String programId) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("programId", uuidV7());
        data.put("programName", "신입사원 온보딩");
        data.put("programDescription", "신입사원을 위한 기본 교육 프로그램입니다.");
        data.put("category", "BASIC_TRAINING");
        data.put("trainingHour", 4);
        data.put("isOnline", true);
        data.put("startDate", LocalDate.of(2025, 10, 7));
        data.put("statusCode", "IN_PROGRESS");

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
        row.put("employeeId", uuidV7());
        row.put("employeeName", employeeName);
        row.put("department", department);
        row.put("position", position);
        row.put("statusCode", status);
        row.put("completedAt", completedAt);
        return row;
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

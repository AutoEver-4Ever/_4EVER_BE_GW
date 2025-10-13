package org.ever._4ever_be_gw.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
@RequestMapping("/business/training")
@Tag(name = "Training Management", description = "직원 교육 관리 API")
public class TrainingController {

    // 직원 교육 현황 목록 조회
    // GET /api/business/training?department=&position=&name=&page=&size=
    @GetMapping("")
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
                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"교육 프로그램 상세 정보 조회에 성공했습니다.\",\n  \"data\": {\n    \"programId\": 1,\n    \"programName\": \"신입사원 온보딩\",\n    \"category\": \"BASIC_TRAINING\",\n    \"trainingHour\": 4,\n    \"isOnline\": true,\n    \"startDate\": \"2025-10-07T14:30:00Z\",\n    \"status\": \"IN_PROGRESS\",\n    \"designatedEmployee\": [{\n      \"employeeId\": 1,\n      \"employeeName\": \"김신입\",\n      \"department\": \"개발팀\",\n      \"position\": \"사원\",\n      \"status\": \"INCOMPLETED\",\n      \"completedAt\": null\n    }]}\n  }\n}"))
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

        Map<String, Object> pageMeta = new LinkedHashMap<>();
        pageMeta.put("number", page);
        pageMeta.put("size", size);
        pageMeta.put("totalElements", items.size());
        pageMeta.put("totalPages", 1);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("items", items);
        response.put("page", pageMeta);
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
        row.put("date", date.atStartOfDay());
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
        response.put("totalPages", 5);
        response.put("totalElements", 48);
        response.put("last", page >= 4);
        response.put("size", size);
        response.put("number", page);
        response.put("sort", sort);
        response.put("numberOfElements", content.size());
        response.put("first", page == 0);
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
        data.put("category", "BASIC_TRAINING");
        data.put("trainingHour", 4);
        data.put("isOnline", true);
        data.put("startDate", java.time.LocalDateTime.of(2025, 10, 7, 14, 30, 0));
        data.put("status", "IN_PROGRESS");

        List<Map<String, Object>> designated = new ArrayList<>();
        designated.add(buildDesignatedEmployee(1L, "김신입", "개발팀", "사원", "INCOMPLETED", null));
        designated.add(buildDesignatedEmployee(10L, "이초롱", "마케팅팀", "사원", "COMPLETED",
            java.time.LocalDateTime.of(2025, 10, 14, 18, 0, 0)));
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
        java.time.LocalDateTime completedAt
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



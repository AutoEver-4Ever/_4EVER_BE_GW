package org.ever._4ever_be_gw.business.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.business.service.HrmHttpService;
import org.ever._4ever_be_gw.common.response.ApiResponse;
import org.ever._4ever_be_gw.config.webclient.ApiClientKey;
import org.ever._4ever_be_gw.config.webclient.WebClientProvider;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class HrmHttpServiceImpl implements HrmHttpService {

    private final WebClientProvider webClientProvider;

    // ==================== Statistics ====================

    @Override
    public ResponseEntity<ApiResponse<Object>> getHRStatistics() {
        log.debug("HR 통계 조회 요청");

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri("/hrm/statistics")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("HR 통계 조회 성공");
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("HR 통계 조회", ex);
        } catch (Exception e) {
            log.error("HR 통계 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("HR 통계 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    // ==================== Departments ====================

    @Override
    public ResponseEntity<ApiResponse<Object>> getDepartmentList(String status, Integer page, Integer size) {
        log.debug("부서 목록 조회 요청 - status: {}, page: {}, size: {}", status, page, size);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/hrm/departments");
                        if (status != null) builder.queryParam("status", status);
                        builder.queryParam("page", page != null ? page : 0);
                        builder.queryParam("size", size != null ? size : 20);
                        return builder.build();
                    })
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("부서 목록 조회 성공");
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("부서 목록 조회", ex);
        } catch (Exception e) {
            log.error("부서 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("부서 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getDepartmentDetail(String departmentId) {
        log.debug("부서 상세 조회 요청 - departmentId: {}", departmentId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri("/hrm/organization/department/{departmentId}", departmentId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("부서 상세 조회 성공 - departmentId: {}", departmentId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("부서 상세 조회", ex);
        } catch (Exception e) {
            log.error("부서 상세 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("부서 상세 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getAllDepartmentsSimple() {
        log.debug("전체 부서 목록 조회 요청 (ID, Name만)");

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri("/hrm/departments/simple")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("전체 부서 목록 조회 성공");
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("전체 부서 목록 조회", ex);
        } catch (Exception e) {
            log.error("전체 부서 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("전체 부서 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getDepartmentMembers(String departmentId) {
        log.debug("부서 구성원 목록 조회 요청 - departmentId: {}", departmentId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri("/hrm/departments/{departmentId}/members", departmentId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("부서 구성원 목록 조회 성공 - departmentId: {}", departmentId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("부서 구성원 목록 조회", ex);
        } catch (Exception e) {
            log.error("부서 구성원 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("부서 구성원 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    // ==================== Positions ====================

    @Override
    public ResponseEntity<ApiResponse<Object>> getPositionList() {
        log.debug("직급 목록 조회 요청");

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri("/hrm/positions")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("직급 목록 조회 성공");
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("직급 목록 조회", ex);
        } catch (Exception e) {
            log.error("직급 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("직급 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getPositionsByDepartmentId(String departmentId) {
        log.debug("부서별 직급 목록 조회 요청 - departmentId: {}", departmentId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri("/hrm/" + departmentId + "/positions/all")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("부서별 직급 목록 조회 성공 - departmentId: {}", departmentId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("부서별 직급 목록 조회", ex);
        } catch (Exception e) {
            log.error("부서별 직급 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("부서별 직급 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getPositionDetail(String positionId) {
        log.debug("직급 상세 조회 요청 - positionId: {}", positionId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri("/hrm/organization/position/{positionId}", positionId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("직급 상세 조회 성공 - positionId: {}", positionId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("직급 상세 조회", ex);
        } catch (Exception e) {
            log.error("직급 상세 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("직급 상세 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    // ==================== Employees ====================

    @Override
    public ResponseEntity<ApiResponse<Object>> getEmployeeList(
            String departmentId, String positionId, String name, Integer page, Integer size) {
        log.debug("직원 목록 조회 요청 - departmentId: {}, positionId: {}, name: {}, page: {}, size: {}",
                departmentId, positionId, name, page, size);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/hrm/employee");
                        if (departmentId != null) builder.queryParam("departmentId", departmentId);
                        if (positionId != null) builder.queryParam("positionId", positionId);
                        if (name != null) builder.queryParam("name", name);
                        builder.queryParam("page", page != null ? page : 0);
                        builder.queryParam("size", size != null ? size : 20);
                        return builder.build();
                    })
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("직원 목록 조회 성공");
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("직원 목록 조회", ex);
        } catch (Exception e) {
            log.error("직원 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("직원 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getEmployeeDetail(String employeeId) {
        log.debug("직원 상세 조회 요청 - employeeId: {}", employeeId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri("/hrm/employee/{employeeId}", employeeId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("직원 상세 조회 성공 - employeeId: {}", employeeId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("직원 상세 조회", ex);
        } catch (Exception e) {
            log.error("직원 상세 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("직원 상세 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getEmployeeWithTrainingByInternelUserId(String internelUserId) {
        log.debug("InternelUser ID로 직원 정보 및 교육 이력 조회 요청 - internelUserId: {}", internelUserId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri("/hrm/employees/{internelUserId}", internelUserId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("InternelUser ID로 직원 정보 및 교육 이력 조회 성공 - internelUserId: {}", internelUserId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("InternelUser ID로 직원 정보 및 교육 이력 조회", ex);
        } catch (Exception e) {
            log.error("InternelUser ID로 직원 정보 및 교육 이력 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("InternelUser ID로 직원 정보 및 교육 이력 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getAvailableTrainingsByInternelUserId(String internelUserId) {
        log.debug("InternelUser ID로 수강 가능한 교육 프로그램 목록 조회 요청 - internelUserId: {}", internelUserId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri("/hrm/employees/{internelUserId}/available-trainings", internelUserId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("InternelUser ID로 수강 가능한 교육 프로그램 목록 조회 성공 - internelUserId: {}", internelUserId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("InternelUser ID로 수강 가능한 교육 프로그램 목록 조회", ex);
        } catch (Exception e) {
            log.error("InternelUser ID로 수강 가능한 교육 프로그램 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("InternelUser ID로 수강 가능한 교육 프로그램 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getCustomerUserDetailByUserId(String customerUserId) {
        log.debug("CustomerUser ID로 고객 사용자 상세 정보 조회 요청 - customerUserId: {}", customerUserId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri("/hrm/customers/by-customer-user/{customerUserId}", customerUserId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("CustomerUser ID로 고객 사용자 상세 정보 조회 성공 - customerUserId: {}", customerUserId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("CustomerUser ID로 고객 사용자 상세 정보 조회", ex);
        } catch (Exception e) {
            log.error("CustomerUser ID로 고객 사용자 상세 정보 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("CustomerUser ID로 고객 사용자 상세 정보 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> updateEmployee(String employeeId, Map<String, Object> requestBody) {
        log.debug("직원 정보 수정 요청 - employeeId: {}, body: {}", employeeId, requestBody);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.patch()
                    .uri("/hrm/employee/{employeeId}", employeeId)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("직원 정보 수정 성공 - employeeId: {}", employeeId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("직원 정보 수정", ex);
        } catch (Exception e) {
            log.error("직원 정보 수정 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("직원 정보 수정 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> requestTraining(Map<String, Object> requestBody) {
        log.debug("교육 프로그램 신청 요청 - body: {}", requestBody);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.post()
                    .uri("/hrm/employee/request")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("교육 프로그램 신청 성공");
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("교육 프로그램 신청", ex);
        } catch (Exception e) {
            log.error("교육 프로그램 신청 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("교육 프로그램 신청 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> enrollTrainingProgram(String employeeId, Map<String, Object> requestBody) {
        log.debug("교육 프로그램 등록 요청 - employeeId: {}, body: {}", employeeId, requestBody);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.post()
                    .uri("/hrm/program/{employeeId}", employeeId)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("교육 프로그램 등록 성공 - employeeId: {}", employeeId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("교육 프로그램 등록", ex);
        } catch (Exception e) {
            log.error("교육 프로그램 등록 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("교육 프로그램 등록 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    // ==================== Leave Requests ====================

    @Override
    public ResponseEntity<ApiResponse<Object>> getLeaveRequestList(
            String department, String position, String name, String type, String sortOrder, Integer page, Integer size) {
        log.debug("휴가 신청 목록 조회 요청 - department: {}, position: {}, name: {}, type: {}, sortOrder: {}, page: {}, size: {}",
                department, position, name, type, sortOrder, page, size);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/hrm/leave/request");
                        if (department != null) builder.queryParam("department", department);
                        if (position != null) builder.queryParam("position", position);
                        if (name != null) builder.queryParam("name", name);
                        if (type != null) builder.queryParam("type", type);
                        if (sortOrder != null) builder.queryParam("sortOrder", sortOrder);
                        builder.queryParam("page", page != null ? page : 0);
                        builder.queryParam("size", size != null ? size : 10);
                        return builder.build();
                    })
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("휴가 신청 목록 조회 성공");
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("휴가 신청 목록 조회", ex);
        } catch (Exception e) {
            log.error("휴가 신청 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("휴가 신청 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> createLeaveRequest(Map<String, Object> requestBody) {
        log.debug("휴가 신청 요청 - body: {}", requestBody);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.post()
                    .uri("/hrm/leave/request")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("휴가 신청 성공");
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("휴가 신청", ex);
        } catch (Exception e) {
            log.error("휴가 신청 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("휴가 신청 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> approveLeaveRequest(String requestId) {
        log.debug("휴가 신청 승인 요청 - requestId: {}", requestId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.patch()
                    .uri("/hrm/leave/request/{requestId}/release", requestId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("휴가 신청 승인 성공 - requestId: {}", requestId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("휴가 신청 승인", ex);
        } catch (Exception e) {
            log.error("휴가 신청 승인 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("휴가 신청 승인 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> rejectLeaveRequest(String requestId) {
        log.debug("휴가 신청 반려 요청 - requestId: {}", requestId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.patch()
                    .uri("/hrm/leave/request/{requestId}/reject", requestId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("휴가 신청 반려 성공 - requestId: {}", requestId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("휴가 신청 반려", ex);
        } catch (Exception e) {
            log.error("휴가 신청 반려 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("휴가 신청 반려 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    // ==================== Payroll ====================

    @Override
    public ResponseEntity<ApiResponse<Object>> getPaystubDetail(String payrollId) {
        log.debug("급여 명세서 상세 조회 요청 - payrollId: {}", payrollId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri("/hrm/payroll/{payrollId}", payrollId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("급여 명세서 상세 조회 성공 - payrollId: {}", payrollId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("급여 명세서 상세 조회", ex);
        } catch (Exception e) {
            log.error("급여 명세서 상세 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("급여 명세서 상세 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getPayrollList(
            Integer year, Integer month, String name, String department, String position, Integer page, Integer size) {
        log.debug("급여 명세서 목록 조회 요청 - year: {}, month: {}, name: {}, department: {}, position: {}, page: {}, size: {}",
                year, month, name, department, position, page, size);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/hrm/payroll");
                        if (year != null) builder.queryParam("year", year);
                        if (month != null) builder.queryParam("month", month);
                        if (name != null) builder.queryParam("name", name);
                        if (department != null) builder.queryParam("department", department);
                        if (position != null) builder.queryParam("position", position);
                        builder.queryParam("page", page != null ? page : 0);
                        builder.queryParam("size", size != null ? size : 10);
                        return builder.build();
                    })
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("급여 명세서 목록 조회 성공");
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("급여 명세서 목록 조회", ex);
        } catch (Exception e) {
            log.error("급여 명세서 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("급여 명세서 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> completePayroll(Map<String, Object> requestBody) {
        log.debug("급여 지급 완료 처리 요청 - body: {}", requestBody);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.post()
                    .uri("/hrm/payroll/complete")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("급여 지급 완료 처리 성공");
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("급여 지급 완료 처리", ex);
        } catch (Exception e) {
            log.error("급여 지급 완료 처리 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("급여 지급 완료 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> generateMonthlyPayroll() {
        log.debug("모든 직원 당월 급여 생성 요청");

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.post()
                    .uri("/hrm/payroll/generate")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("모든 직원 당월 급여 생성 성공");
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("모든 직원 당월 급여 생성", ex);
        } catch (Exception e) {
            log.error("모든 직원 당월 급여 생성 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("모든 직원 당월 급여 생성 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getAllPayrollStatuses() {
        log.debug("급여 상태 목록 조회 요청");

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri("/hrm/payroll/statuses")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("급여 상태 목록 조회 성공");
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("급여 상태 목록 조회", ex);
        } catch (Exception e) {
            log.error("급여 상태 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("급여 상태 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    // ==================== Attendance ====================

    @Override
    public ResponseEntity<ApiResponse<Object>> getAllAttendanceStatuses() {
        log.debug("출퇴근 상태 목록 조회 요청");

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri("/hrm/attendance/statuses")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("출퇴근 상태 목록 조회 성공");
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("출퇴근 상태 목록 조회", ex);
        } catch (Exception e) {
            log.error("출퇴근 상태 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("출퇴근 상태 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    // ==================== Training ====================

    @Override
    public ResponseEntity<ApiResponse<Object>> getProgramDetailInfo(String programId) {
        log.debug("교육 프로그램 상세 정보 조회 요청 - programId: {}", programId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri("/hrm/trainings/program/{programId}", programId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("교육 프로그램 상세 정보 조회 성공 - programId: {}", programId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("교육 프로그램 상세 정보 조회", ex);
        } catch (Exception e) {
            log.error("교육 프로그램 상세 정보 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("교육 프로그램 상세 정보 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getTrainingList(
            String name, String status, String category, Integer page, Integer size) {
        log.debug("교육 프로그램 목록 조회 요청 - name: {}, status: {}, category: {}, page: {}, size: {}",
                name, status, category, page, size);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/hrm/trainings/program");
                        if (name != null) builder.queryParam("name", name);
                        if (status != null) builder.queryParam("status", status);
                        if (category != null) builder.queryParam("category", category);
                        builder.queryParam("page", page != null ? page : 0);
                        builder.queryParam("size", size != null ? size : 10);
                        return builder.build();
                    })
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("교육 프로그램 목록 조회 성공");
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("교육 프로그램 목록 조회", ex);
        } catch (Exception e) {
            log.error("교육 프로그램 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("교육 프로그램 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> createTrainingProgram(Map<String, Object> requestBody) {
        log.debug("교육 프로그램 생성 요청 - body: {}", requestBody);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.post()
                    .uri("/hrm/trainings/program")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("교육 프로그램 생성 성공");
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("교육 프로그램 생성", ex);
        } catch (Exception e) {
            log.error("교육 프로그램 생성 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("교육 프로그램 생성 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> updateTrainingProgram(String programId, Map<String, Object> requestBody) {
        log.debug("교육 프로그램 수정 요청 - programId: {}, body: {}", programId, requestBody);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.patch()
                    .uri("/hrm/program/{programId}", programId)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("교육 프로그램 수정 성공 - programId: {}", programId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("교육 프로그램 수정", ex);
        } catch (Exception e) {
            log.error("교육 프로그램 수정 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("교육 프로그램 수정 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getAllTrainingCategories() {
        log.debug("교육 카테고리 목록 조회 요청");

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri("/hrm/trainings/categories")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("교육 카테고리 목록 조회 성공");
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("교육 카테고리 목록 조회", ex);
        } catch (Exception e) {
            log.error("교육 카테고리 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("교육 카테고리 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getAllTrainingPrograms() {
        log.debug("전체 교육 프로그램 목록 조회 요청");

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri("/hrm/trainings/programs")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("전체 교육 프로그램 목록 조회 성공");
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("전체 교육 프로그램 목록 조회", ex);
        } catch (Exception e) {
            log.error("전체 교육 프로그램 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("전체 교육 프로그램 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getAllTrainingCompletionStatuses() {
        log.debug("교육 완료 상태 목록 조회 요청");

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri("/hrm/trainings/completion-statuses")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("교육 완료 상태 목록 조회 성공");
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("교육 완료 상태 목록 조회", ex);
        } catch (Exception e) {
            log.error("교육 완료 상태 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("교육 완료 상태 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getEmployeeTrainingHistory(String employeeId) {
        log.debug("직원 교육 이력 조회 요청 - employeeId: {}", employeeId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri("/hrm/trainings/employee/{employeeId}/training-history", employeeId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("직원 교육 이력 조회 성공 - employeeId: {}", employeeId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("직원 교육 이력 조회", ex);
        } catch (Exception e) {
            log.error("직원 교육 이력 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("직원 교육 이력 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getEmployeeTrainingList(
            String department, String position, String name, Integer page, Integer size) {
        log.debug("직원 교육 현황 목록 조회 요청 - department: {}, position: {}, name: {}, page: {}, size: {}",
                department, position, name, page, size);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/hrm/trainings");
                        if (department != null) builder.queryParam("department", department);
                        if (position != null) builder.queryParam("position", position);
                        if (name != null) builder.queryParam("name", name);
                        builder.queryParam("page", page != null ? page : 0);
                        builder.queryParam("size", size != null ? size : 20);
                        return builder.build();
                    })
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("직원 교육 현황 목록 조회 성공");
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("직원 교육 현황 목록 조회", ex);
        } catch (Exception e) {
            log.error("직원 교육 현황 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("직원 교육 현황 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getTrainingStatusList(
            String department, String position, String name, Integer page, Integer size) {
        log.debug("직원 교육 현황 통계 조회 요청 - department: {}, position: {}, name: {}, page: {}, size: {}",
                department, position, name, page, size);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/hrm/trainings/training-status");
                        if (department != null) builder.queryParam("department", department);
                        if (position != null) builder.queryParam("position", position);
                        if (name != null) builder.queryParam("name", name);
                        builder.queryParam("page", page != null ? page : 0);
                        builder.queryParam("size", size != null ? size : 20);
                        return builder.build();
                    })
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("직원 교육 현황 통계 조회 성공");
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("직원 교육 현황 통계 조회", ex);
        } catch (Exception e) {
            log.error("직원 교육 현황 통계 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("직원 교육 현황 통계 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getEmployeeTrainingSummary(String employeeId) {
        log.debug("직원별 교육 요약 정보 조회 요청 - employeeId: {}", employeeId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri("/hrm/trainings/training/employee/{employeeId}", employeeId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("직원별 교육 요약 정보 조회 성공 - employeeId: {}", employeeId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("직원별 교육 요약 정보 조회", ex);
        } catch (Exception e) {
            log.error("직원별 교육 요약 정보 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("직원별 교육 요약 정보 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    // ==================== Time Records ====================

    @Override
    public ResponseEntity<ApiResponse<Object>> getTimeRecordDetail(String timerecordId) {
        log.debug("근태 기록 상세 정보 조회 요청 - timerecordId: {}", timerecordId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri("/hrm/time-records/time-record/{timerecordId}", timerecordId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("근태 기록 상세 정보 조회 성공 - timerecordId: {}", timerecordId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("근태 기록 상세 정보 조회", ex);
        } catch (Exception e) {
            log.error("근태 기록 상세 정보 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("근태 기록 상세 정보 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> updateTimeRecord(String timerecordId, Map<String, Object> requestBody) {
        log.debug("근태 기록 수정 요청 - timerecordId: {}, body: {}", timerecordId, requestBody);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.patch()
                    .uri("/hrm/time-records/time-record/{timerecordId}", timerecordId)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("근태 기록 수정 성공 - timerecordId: {}", timerecordId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("근태 기록 수정", ex);
        } catch (Exception e) {
            log.error("근태 기록 수정 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("근태 기록 수정 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getAttendanceList(
            String department, String position, String name, String date, Integer page, Integer size) {
        log.debug("근태 기록 목록 조회 요청 - department: {}, position: {}, name: {}, date: {}, page: {}, size: {}",
                department, position, name, date, page, size);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/hrm/time-records/time-record");
                        if (department != null) builder.queryParam("department", department);
                        if (position != null) builder.queryParam("position", position);
                        if (name != null) builder.queryParam("name", name);
                        if (date != null) builder.queryParam("date", date);
                        builder.queryParam("page", page != null ? page : 0);
                        builder.queryParam("size", size != null ? size : 20);
                        return builder.build();
                    })
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("근태 기록 목록 조회 성공");
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("근태 기록 목록 조회", ex);
        } catch (Exception e) {
            log.error("근태 기록 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("근태 기록 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    // ==================== Attendance ====================

    @Override
    public ResponseEntity<ApiResponse<Object>> getAttendanceHistoryList(
            String employeeId, String startDate, String endDate, String status, Integer page, Integer size) {
        log.debug("출퇴근 기록 조회 요청 - employeeId: {}, startDate: {}, endDate: {}, status: {}, page: {}, size: {}",
                employeeId, startDate, endDate, status, page, size);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/hrm/attendance");
                        if (employeeId != null) builder.queryParam("employeeId", employeeId);
                        if (startDate != null) builder.queryParam("startDate", startDate);
                        if (endDate != null) builder.queryParam("endDate", endDate);
                        if (status != null) builder.queryParam("status", status);
                        builder.queryParam("page", page != null ? page : 0);
                        builder.queryParam("size", size != null ? size : 20);
                        return builder.build();
                    })
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("출퇴근 기록 조회 성공");
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("출퇴근 기록 조회", ex);
        } catch (Exception e) {
            log.error("출퇴근 기록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("출퇴근 기록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> checkIn(String internelUserId) {
        log.debug("출근 처리 요청 - internelUserId: {}", internelUserId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            Map<String, Object> requestBody = Map.of("employeeId", internelUserId);

            ApiResponse<Object> response = businessClient.patch()
                    .uri("/hrm/attendance/check-in")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("출근 처리 성공 - internelUserId: {}", internelUserId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("출근 처리", ex);
        } catch (Exception e) {
            log.error("출근 처리 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("출근 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> checkOut(String internelUserId) {
        log.debug("퇴근 처리 요청 - internelUserId: {}", internelUserId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            Map<String, Object> requestBody = Map.of("employeeId", internelUserId);

            ApiResponse<Object> response = businessClient.patch()
                    .uri("/hrm/attendance/check-out")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("퇴근 처리 성공 - internelUserId: {}", internelUserId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("퇴근 처리", ex);
        } catch (Exception e) {
            log.error("퇴근 처리 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("퇴근 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> checkInByInternelUserId(String internelUserId) {
        log.debug("InternelUser ID로 출근 처리 요청 - internelUserId: {}", internelUserId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            Map<String, Object> requestBody = Map.of("employeeId", internelUserId);

            ApiResponse<Object> response = businessClient.patch()
                    .uri("/hrm/attendance/check-in-by-internel-user")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("InternelUser ID로 출근 처리 성공 - internelUserId: {}", internelUserId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("InternelUser ID로 출근 처리", ex);
        } catch (Exception e) {
            log.error("InternelUser ID로 출근 처리 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("InternelUser ID로 출근 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> checkOutByInternelUserId(String internelUserId) {
        log.debug("InternelUser ID로 퇴근 처리 요청 - internelUserId: {}", internelUserId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            Map<String, Object> requestBody = Map.of("employeeId", internelUserId);

            ApiResponse<Object> response = businessClient.patch()
                    .uri("/hrm/attendance/check-out-by-internel-user")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("InternelUser ID로 퇴근 처리 성공 - internelUserId: {}", internelUserId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("InternelUser ID로 퇴근 처리", ex);
        } catch (Exception e) {
            log.error("InternelUser ID로 퇴근 처리 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("InternelUser ID로 퇴근 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getAttendanceRecordsByInternelUserId(String internelUserId) {
        log.debug("InternelUser ID로 출퇴근 기록 목록 조회 요청 - internelUserId: {}", internelUserId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri("/hrm/employees/{internelUserId}/attendance-records", internelUserId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("InternelUser ID로 출퇴근 기록 목록 조회 성공 - internelUserId: {}", internelUserId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("InternelUser ID로 출퇴근 기록 목록 조회", ex);
        } catch (Exception e) {
            log.error("InternelUser ID로 출퇴근 기록 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("InternelUser ID로 출퇴근 기록 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    /**
     * WebClient 오류를 처리하고 로깅하는 공통 메서드
     */
    private ResponseEntity<ApiResponse<Object>> handleWebClientError(String operation, WebClientResponseException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String errorBody = ex.getResponseBodyAsString();

        log.error("{} 실패 - Status: {}, Body: {}", operation, ex.getStatusCode(), errorBody);

        return ResponseEntity.status(status).body(
                ApiResponse.fail(operation + " 중 오류가 발생했습니다.", status, null)
        );
    }
}

package org.ever._4ever_be_gw.business.service;

import org.ever._4ever_be_gw.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * HRM(인적자원관리) HTTP 서비스 인터페이스
 * Business 서비스의 HRM 엔드포인트와 통신
 */
public interface HrmHttpService {

    // ==================== Statistics ====================

    /**
     * HR 대시보드 통계 조회
     */
    ResponseEntity<ApiResponse<Object>> getHRStatistics();

    // ==================== Departments ====================

    /**
     * 부서 목록 조회
     */
    ResponseEntity<ApiResponse<Object>> getDepartmentList(String status, Integer page, Integer size);

    /**
     * 부서 상세 정보 조회
     */
    ResponseEntity<ApiResponse<Object>> getDepartmentDetail(String departmentId);

    /**
     * 전체 부서 목록 조회 (ID, Name만)
     */
    ResponseEntity<ApiResponse<Object>> getAllDepartmentsSimple();

    /**
     * 부서 구성원 목록 조회 (ID, Name만)
     */
    ResponseEntity<ApiResponse<Object>> getDepartmentMembers(String departmentId);

    // ==================== Positions ====================

    /**
     * 직급 목록 조회
     */
    ResponseEntity<ApiResponse<Object>> getPositionList();

    /**
     * 직급 상세 정보 조회
     */
    ResponseEntity<ApiResponse<Object>> getPositionDetail(String positionId);

    /**
     * 전체 직급 목록 조회 (ID, Name만)
     */
    ResponseEntity<ApiResponse<Object>> getAllPositionsSimple();

    // ==================== Employees ====================

    /**
     * 직원 목록 조회
     */
    ResponseEntity<ApiResponse<Object>> getEmployeeList(
            String department, String position, String name, Integer page, Integer size);

    /**
     * 직원 상세 정보 조회
     */
    ResponseEntity<ApiResponse<Object>> getEmployeeDetail(String employeeId);

    /**
     * InternelUser ID로 직원 정보 및 교육 이력 조회
     */
    ResponseEntity<ApiResponse<Object>> getEmployeeWithTrainingByInternelUserId(String internelUserId);

    /**
     * InternelUser ID로 수강 가능한 교육 프로그램 목록 조회
     */
    ResponseEntity<ApiResponse<Object>> getAvailableTrainingsByInternelUserId(String internelUserId);

    /**
     * CustomerUser ID로 고객 사용자 상세 정보 조회
     */
    ResponseEntity<ApiResponse<Object>> getCustomerUserDetailByUserId(String customerUserId);

    /**
     * 직원 정보 수정
     */
    ResponseEntity<ApiResponse<Object>> updateEmployee(String employeeId, Map<String, Object> requestBody);

    /**
     * 교육 프로그램 신청
     */
    ResponseEntity<ApiResponse<Object>> requestTraining(Map<String, Object> requestBody);

    /**
     * 직원 교육 프로그램 등록
     */
    ResponseEntity<ApiResponse<Object>> enrollTrainingProgram(String employeeId, Map<String, Object> requestBody);

    // ==================== Leave Requests ====================

    /**
     * 휴가 신청 목록 조회
     */
    ResponseEntity<ApiResponse<Object>> getLeaveRequestList(
            String department, String position, String name, String type, String sortOrder, Integer page, Integer size);

    /**
     * 휴가 신청
     */
    ResponseEntity<ApiResponse<Object>> createLeaveRequest(Map<String, Object> requestBody);

    /**
     * 휴가 신청 승인
     */
    ResponseEntity<ApiResponse<Object>> approveLeaveRequest(String requestId);

    /**
     * 휴가 신청 반려
     */
    ResponseEntity<ApiResponse<Object>> rejectLeaveRequest(String requestId);

    // ==================== Payroll ====================

    /**
     * 급여 명세서 상세 조회
     */
    ResponseEntity<ApiResponse<Object>> getPaystubDetail(String payrollId);

    /**
     * 급여 명세서 목록 조회
     */
    ResponseEntity<ApiResponse<Object>> getPayrollList(
            Integer year, Integer month, String name, String department, String position, Integer page, Integer size);

    /**
     * 급여 지급 완료 처리
     */
    ResponseEntity<ApiResponse<Object>> completePayroll(Map<String, Object> requestBody);

    /**
     * 모든 직원 당월 급여 생성
     */
    ResponseEntity<ApiResponse<Object>> generateMonthlyPayroll();

    /**
     * 급여 상태 목록 조회 (enum 전체)
     */
    ResponseEntity<ApiResponse<Object>> getAllPayrollStatuses();

    // ==================== Attendance ====================

    /**
     * 출퇴근 상태 목록 조회 (enum 전체)
     */
    ResponseEntity<ApiResponse<Object>> getAllAttendanceStatuses();

    // ==================== Training ====================

    /**
     * 교육 프로그램 상세 정보 조회
     */
    ResponseEntity<ApiResponse<Object>> getProgramDetailInfo(String programId);

    /**
     * 교육 프로그램 목록 조회
     */
    ResponseEntity<ApiResponse<Object>> getTrainingList(
            String name, String status, String category, Integer page, Integer size);

    /**
     * 교육 프로그램 생성
     */
    ResponseEntity<ApiResponse<Object>> createTrainingProgram(Map<String, Object> requestBody);

    /**
     * 교육 프로그램 수정
     */
    ResponseEntity<ApiResponse<Object>> updateTrainingProgram(String programId, Map<String, Object> requestBody);

    /**
     * 교육 카테고리 목록 조회 (enum 전체)
     */
    ResponseEntity<ApiResponse<Object>> getAllTrainingCategories();

    /**
     * 전체 교육 프로그램 목록 조회 (ID, Name만)
     */
    ResponseEntity<ApiResponse<Object>> getAllTrainingPrograms();

    /**
     * 교육 완료 상태 목록 조회
     */
    ResponseEntity<ApiResponse<Object>> getAllTrainingCompletionStatuses();

    /**
     * 직원 교육 이력 조회
     */
    ResponseEntity<ApiResponse<Object>> getEmployeeTrainingHistory(String employeeId);

    /**
     * 직원 교육 현황 목록 조회
     */
    ResponseEntity<ApiResponse<Object>> getEmployeeTrainingList(
            String department, String position, String name, Integer page, Integer size);

    /**
     * 직원 교육 현황 통계 조회
     */
    ResponseEntity<ApiResponse<Object>> getTrainingStatusList(
            String department, String position, String name, Integer page, Integer size);

    /**
     * 직원별 교육 요약 정보 조회
     */
    ResponseEntity<ApiResponse<Object>> getEmployeeTrainingSummary(String employeeId);

    // ==================== Time Records ====================

    /**
     * 근태 기록 상세 정보 조회
     */
    ResponseEntity<ApiResponse<Object>> getTimeRecordDetail(String timerecordId);

    /**
     * 근태 기록 수정
     */
    ResponseEntity<ApiResponse<Object>> updateTimeRecord(String timerecordId, Map<String, Object> requestBody);

    /**
     * 근태 기록 목록 조회
     */
    ResponseEntity<ApiResponse<Object>> getAttendanceList(
            String department, String position, String name, String date, Integer page, Integer size);

    // ==================== Attendance ====================

    /**
     * 출퇴근 기록 조회
     */
    ResponseEntity<ApiResponse<Object>> getAttendanceHistoryList(
            String employeeId, String startDate, String endDate, String status, Integer page, Integer size);

    /**
     * 출근 처리 (InternelUser ID 기반)
     */
    ResponseEntity<ApiResponse<Object>> checkIn(String internelUserId);

    /**
     * 퇴근 처리 (InternelUser ID 기반)
     */
    ResponseEntity<ApiResponse<Object>> checkOut(String internelUserId);

    /**
     * InternelUser ID로 출근 처리
     */
    ResponseEntity<ApiResponse<Object>> checkInByInternelUserId(String internelUserId);

    /**
     * InternelUser ID로 퇴근 처리
     */
    ResponseEntity<ApiResponse<Object>> checkOutByInternelUserId(String internelUserId);

    /**
     * InternelUser ID로 출퇴근 기록 목록 조회
     */
    ResponseEntity<ApiResponse<Object>> getAttendanceRecordsByInternelUserId(String internelUserId);
}

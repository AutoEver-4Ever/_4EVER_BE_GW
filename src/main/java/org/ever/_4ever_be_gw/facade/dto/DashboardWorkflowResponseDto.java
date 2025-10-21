package org.ever._4ever_be_gw.facade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 대시보드 워크플로우 응답 DTO
 * - 요청한 사용자 역할과 탭 배열 구조를 함께 반환합니다.
 *   tabs: [{ code, label, items }]
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardWorkflowResponseDto {
    private String role;                       // 요청한 사용자 역할 (예: MM_USER, HRM_ADMIN)
    private List<DashboardWorkflowTabDto> tabs; // 탭 배열 (각 탭: code, label, items)
}

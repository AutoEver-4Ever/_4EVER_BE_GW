package org.ever._4ever_be_gw.business.service;

import org.ever._4ever_be_gw.business.dto.employee.EmployeeCreateRequestDto;
import org.ever._4ever_be_gw.business.dto.hrm.UserCreateResponseDto;
import reactor.core.publisher.Mono;

/**
 * HRM 서비스 인터페이스
 */
public interface HrmService {

    /**
     * 내부 사용자(직원) 등록
     */
    Mono<UserCreateResponseDto> createInternalUser(EmployeeCreateRequestDto requestDto);
}

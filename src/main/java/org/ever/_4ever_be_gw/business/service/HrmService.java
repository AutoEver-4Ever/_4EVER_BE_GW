package org.ever._4ever_be_gw.business.service;

import org.ever._4ever_be_gw.business.dto.employee.EmployeeCreateRequestDto;
import reactor.core.publisher.Mono;

public interface HrmService {
    // 내부 사용자 등록
    // Mono: 비동기 작업 결과가 있거나 없을 수 있음.
    Mono<EmployeeCreateRequestDto> createInternalUser(EmployeeCreateRequestDto requestDto);
}

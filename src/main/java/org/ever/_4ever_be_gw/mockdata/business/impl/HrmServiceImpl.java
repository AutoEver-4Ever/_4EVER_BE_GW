//package org.ever._4ever_be_gw.mockdata.business.impl;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.ever._4ever_be_gw.business.dto.employee.EmployeeCreateRequestDto;
//import org.ever._4ever_be_gw.business.dto.hrm.UserCreateResponseDto;
//import org.ever._4ever_be_gw.mockdata.business.service.HrmService;
//import org.ever._4ever_be_gw.config.webclient.ApiClientKey;
//import org.ever._4ever_be_gw.config.webclient.WebClientProvider;
//import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient;
//import org.springframework.web.reactive.function.client.WebClientResponseException;
//import reactor.core.publisher.Mono;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class HrmServiceImpl implements HrmService {
//
//    private final WebClientProvider webClientProvider;
//
//    @Override
//    public Mono<UserCreateResponseDto> createInternalUser(EmployeeCreateRequestDto requestDto) {
//        log.debug("내부 사용자 등록 요청, payload: {}", requestDto);
//
//        WebClient businessWebClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);
//
//        return businessWebClient.post()
//            .uri("/hrm/internal-users")
//            .bodyValue(requestDto)
//            .retrieve()
//            .bodyToMono(UserCreateResponseDto.class)
//            .doOnSuccess(response -> log.info("내부 사용자 등록 성공: {}", response))
//            .doOnError(err -> log.error("내부 사용자 등록 실패: {}", err.getMessage()))
//            .onErrorResume(WebClientResponseException.class, ex -> {
//                log.error(
//                    "[ERROR][HRM] HRM 서버 응답 오류, Status: {}, Body: {}",
//                    ex.getStatusCode(),
//                    ex.getResponseBodyAsString()
//                );
//                return Mono.error(
//                    new RuntimeException(
//                        "[ERROR][HRM] HRM 서버에서 내부 사용자 등록 중 오류가 발생했습니다.: " + ex.getMessage()
//                    )
//                );
//            });
//    }
//}

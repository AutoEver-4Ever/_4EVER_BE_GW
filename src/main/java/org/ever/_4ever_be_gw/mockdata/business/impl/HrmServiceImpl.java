//package org.ever._4ever_be_gw.mockdata.business.impl;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.ever._4ever_be_gw.mockdata.business.dto.employee.EmployeeCreateRequestDto;
//import org.ever._4ever_be_gw.mockdata.business.dto.hrm.CreateAuthUserResultDto;
//import org.ever._4ever_be_gw.mockdata.business.service.HrmService;
//import org.ever._4ever_be_gw.config.webclient.ApiClientKey;
//import org.ever._4ever_be_gw.config.webclient.WebClientProvider;
//import org.ever._4ever_be_gw.common.dto.RemoteApiResponse;
//import org.ever._4ever_be_gw.common.exception.RemoteApiException;
//import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.http.HttpStatus;
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
//    public Mono<RemoteApiResponse<CreateAuthUserResultDto>> createInternalUser(EmployeeCreateRequestDto requestDto) {
//        log.debug("내부 사용자 등록 요청, payload: {}", requestDto);
//
//        WebClient businessWebClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);
//
//        return businessWebClient.post()
//            .uri("/hrm/employee-users")
//            .bodyValue(requestDto)
//            .retrieve()
//            .bodyToMono(new ParameterizedTypeReference<RemoteApiResponse<CreateAuthUserResultDto>>() {})
//            .flatMap(response -> {
//                if (!response.isSuccess()) {
//                    HttpStatus status = HttpStatus.resolve(response.getStatus());
//                    if (status == null) {
//                        status = HttpStatus.INTERNAL_SERVER_ERROR;
//                    }
//                    return Mono.error(
//                        new RemoteApiException(
//                            status,
//                            response.getMessage(),
//                            response.getErrors()
//                        )
//                    );
//                }
//                return Mono.just(response);
//            })
//            .doOnSuccess(response -> log.info("내부 사용자 등록 성공 - status: {}, data: {}", response.getStatus(), response.getData()))
//            .doOnError(err -> log.error("내부 사용자 등록 실패: {}", err.getMessage()))
//            .onErrorResume(WebClientResponseException.class, ex -> {
//                log.error(
//                    "[ERROR][HRM] HRM 서버 응답 오류, Status: {}, Body: {}",
//                    ex.getStatusCode(),
//                    ex.getResponseBodyAsString()
//                );
//                return Mono.error(
//                    new RemoteApiException(
//                        HttpStatus.INTERNAL_SERVER_ERROR,
//                        "[ERROR][HRM] HRM 서버에서 내부 사용자 등록 중 오류가 발생했습니다.",
//                        ex.getResponseBodyAsString()
//                    )
//                );
//            });
//    }
//}

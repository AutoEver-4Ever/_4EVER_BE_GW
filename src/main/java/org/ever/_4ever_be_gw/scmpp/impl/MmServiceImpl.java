package org.ever._4ever_be_gw.scmpp.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.business.dto.hrm.CreateAuthUserResultDto;
import org.ever._4ever_be_gw.common.dto.RemoteApiResponse;
import org.ever._4ever_be_gw.common.exception.RemoteApiException;
import org.ever._4ever_be_gw.config.webclient.ApiClientKey;
import org.ever._4ever_be_gw.config.webclient.WebClientProvider;
import org.ever._4ever_be_gw.scmpp.dto.mm.supplier.SupplierCreateRequestDto;
import org.ever._4ever_be_gw.scmpp.service.MmService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class MmServiceImpl implements MmService {

    private final WebClientProvider webClientProvider;

    @Override
    public Mono<RemoteApiResponse<CreateAuthUserResultDto>> createSupplier(SupplierCreateRequestDto requestDto) {
        WebClient scmPpClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        return scmPpClient.post()
            .uri("/mm/supplier")
            .bodyValue(requestDto)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<RemoteApiResponse<CreateAuthUserResultDto>>() {})
            .flatMap(response -> {
                if (!response.isSuccess()) {
                    HttpStatus status = HttpStatus.resolve(response.getStatus());
                    if (status == null) {
                        status = HttpStatus.INTERNAL_SERVER_ERROR;
                    }
                    return Mono.error(new RemoteApiException(status, response.getMessage(), response.getErrors()));
                }
                return Mono.just(response);
            })
            .doOnSuccess(response -> log.info("[INFO] 공급사 등록 성공 - tx status: {}, userId: {}", response.getStatus(),
                response.getData() != null ? response.getData().getUserId() : null))
            .doOnError(error -> log.error("[ERROR] 공급사 등록 실패: {}", error.getMessage(), error))
            .onErrorResume(WebClientResponseException.class, ex -> {
                log.error("[ERROR] SCM_PP 서버 응답 오류 - status: {}, body: {}",
                    ex.getStatusCode(), ex.getResponseBodyAsString());
                return Mono.error(new RemoteApiException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "[ERROR] 공급사 등록 처리 중 오류가 발생했습니다.",
                    ex.getResponseBodyAsString()
                ));
            });
    }
}

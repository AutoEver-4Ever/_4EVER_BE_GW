package org.ever._4ever_be_gw.business.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.business.dto.customer.CustomerCreateRequestDto;
import org.ever._4ever_be_gw.business.dto.customer.CustomerCreateResponseDto;
import org.ever._4ever_be_gw.business.service.SdService;
import org.ever._4ever_be_gw.config.webclient.ApiClientKey;
import org.ever._4ever_be_gw.config.webclient.WebClientProvider;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class SdServiceImpl implements SdService {


    private final WebClientProvider webClientProvider;

    @Override
    public Mono<CustomerCreateResponseDto> createCustomer(CustomerCreateRequestDto requestDto) {
        WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

        return businessClient.post()
                .uri("/sd/customers")
                .bodyValue(requestDto)
                .retrieve()
                .bodyToMono(CustomerCreateResponseDto.class)
                .doOnSuccess(response ->
                        log.info("[INFO] 고객사 등록 및 담당자 생성 성공. customerId:{}, managerId: {}", response.getCustomerId(), response.getManager().getManagerName())
                )
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.error("[ERROR] 비즈니스 서버 오류 - status: {}, body: {}",
                            ex.getStatusCode(), ex.getResponseBodyAsString());
                    return Mono.error(new RuntimeException("고객사 등록 및 담당자 생성 중 오류가 발생했습니다. : " + ex.getMessage()));
                });
    }
}

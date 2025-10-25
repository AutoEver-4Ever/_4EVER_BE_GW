package org.ever._4ever_be_gw.config.application;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    @Qualifier("serviceUrlByKeyMap")
    private final Map<String, String> serviceUrlByKeyMap;

    @Bean("alarmWebClient")
    public WebClient alarmWebClient(WebClient.Builder builder) {
        return createServiceWebClient(builder, "alarm");
    }

    @Bean("authWebClient")
    public WebClient authWebClient(WebClient.Builder builder) {
        return createServiceWebClient(builder, "auth");
    }

    @Bean("paymentWebClient")
    public WebClient paymentWebClient(WebClient.Builder builder) {
        return createServiceWebClient(builder, "payment");
    }

    @Bean("scmWebClient")
    public WebClient scmWebClient(WebClient.Builder builder) {
        return createServiceWebClient(builder, "scm");
    }

    @Bean("businessWebClient")
    public WebClient businessWebClient(WebClient.Builder builder) {
        return createServiceWebClient(builder, "business");
    }

    @Bean("gatewayWebClient")
    public WebClient gatewayWebClient(WebClient.Builder builder) {
        return createServiceWebClient(builder, "gateway");
    }

    /**
     * 서비스별 WebClient 생성 헬퍼 메서드
     */
    private WebClient createServiceWebClient(WebClient.Builder builder, String serviceKey) {
        String baseUrl = serviceUrlByKeyMap.get(serviceKey);

        if (baseUrl == null) {
            throw new IllegalArgumentException("서비스 키 '" + serviceKey + "'에 해당하는 URL을 찾을 수 없습니다.");
        }

        return builder
            .baseUrl(baseUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }
}
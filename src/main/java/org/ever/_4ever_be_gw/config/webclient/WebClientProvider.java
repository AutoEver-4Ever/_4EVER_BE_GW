package org.ever._4ever_be_gw.config.webclient;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Slf4j
public class WebClientProvider {

    private final WebClient.Builder webClientBuilder;
    private final Map<ApiClientKey, WebClient> cache = new ConcurrentHashMap<>();
    private final ApiProperties apiProperties;
//    private final Map<String, String> baseUrls;

    public WebClientProvider(WebClient.Builder webClientBuilder, ApiProperties apiProperties) {
        this.webClientBuilder = webClientBuilder;
        this.apiProperties = apiProperties;
//        this.baseUrls = apiProperties.getClients().entrySet().stream()
//            .collect(Collectors.toMap(
//                Map.Entry::getKey, e -> e.getValue().getBaseUrl()
//                ApiProperties.Client::getName,
//                ApiProperties.Client::getBaseUrl
//            ));
    }

    public WebClient getWebClient(ApiClientKey clientKey) {
        Objects.requireNonNull(clientKey, "클라이언트 키(clientKey)는 null을 허용하지 않습니다.");

        return cache.computeIfAbsent(clientKey, ck -> {
            var clientProps = apiProperties.getClients().get(ck.getPropertyKey());
            if (clientProps == null) {
                log.error("WebClient 설정 없음 : '{}'", ck.getPropertyKey());
                throw new IllegalArgumentException(
                    "API 클라이언트 키 설정이 잘못되었습니다. : " + ck
                );
            }

            return webClientBuilder
                .clone()
                .baseUrl(clientProps.getBaseUrl())
                .build();
        });
    }

}

package org.ever._4ever_be_gw.config.webclient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Slf4j
public class WebClientProvider {

    private final WebClient.Builder webClientBuilder;
    private final Map<String, WebClient> cache = new ConcurrentHashMap<>();
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

    public WebClient getWebClient(String clientKey) {
        return cache.computeIfAbsent(clientKey, key -> {
            ApiProperties.ClientProperties clientProps = apiProperties.getClients().get(key);
            if (clientProps == null) {
                log.error("WebClient 설정 없음 : '{}'", key);
                throw new IllegalArgumentException(
                    "No API client configuration found for key: " + key
                );
            }
            return webClientBuilder
                .baseUrl(clientProps.getBaseUrl())
                .build();
        });
    }
}

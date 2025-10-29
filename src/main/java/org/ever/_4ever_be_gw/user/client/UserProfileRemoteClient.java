package org.ever._4ever_be_gw.user.client;

import java.time.Duration;
import java.util.Locale;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.common.dto.RemoteApiResponse;
import org.ever._4ever_be_gw.config.webclient.ApiClientKey;
import org.ever._4ever_be_gw.config.webclient.WebClientProvider;
import org.ever._4ever_be_gw.user.dto.CustomerUserProfileResponse;
import org.ever._4ever_be_gw.user.dto.InternalUserProfileResponse;
import org.ever._4ever_be_gw.user.dto.SupplierUserProfileResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * 사용자 유형별 프로필 정보를 원격 서비스에서 조회하는 클라이언트.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserProfileRemoteClient {

    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(5);
    private static final ParameterizedTypeReference<RemoteApiResponse<InternalUserProfileResponse>> INTERNAL_RESPONSE_TYPE =
        new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<RemoteApiResponse<CustomerUserProfileResponse>> CUSTOMER_RESPONSE_TYPE =
        new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<RemoteApiResponse<SupplierUserProfileResponse>> SUPPLIER_RESPONSE_TYPE =
        new ParameterizedTypeReference<>() {};

    private final WebClientProvider webClientProvider;

    public Optional<String> fetchUserName(String userType, String userId) {
        if (!StringUtils.hasText(userType) || !StringUtils.hasText(userId)) {
            return Optional.empty();
        }

        String normalizedType = userType.toUpperCase(Locale.ROOT);

        return switch (normalizedType) {
            case "INTERNAL" -> fetchInternalUserName(userId);
            case "CUSTOMER" -> fetchCustomerUserName(userId);
            case "SUPPLIER" -> fetchSupplierUserName(userId);
            default -> Optional.empty();
        };
    }

    private Optional<String> fetchInternalUserName(String userId) {
        WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);
        try {
            RemoteApiResponse<InternalUserProfileResponse> response = businessClient.post()
                .uri("/hrm/{userId}/employee", userId)
                .retrieve()
                .bodyToMono(INTERNAL_RESPONSE_TYPE)
                .block(REQUEST_TIMEOUT);

            if (response == null || !response.isSuccess() || response.getData() == null) {
                log.warn("[GW][UserProfile] 내부 사용자 이름 조회 실패 - userId: {}, response: {}", userId, response);
                return Optional.empty();
            }

            return Optional.ofNullable(response.getData().name());
        } catch (WebClientResponseException ex) {
            log.error("[GW][UserProfile] 내부 사용자 이름 조회 중 원격 응답 예외 - userId: {}, status: {}, body: {}",
                userId, ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
            return Optional.empty();
        } catch (Exception ex) {
            log.error("[GW][UserProfile] 내부 사용자 이름 조회 중 예기치 못한 오류 - userId: {}", userId, ex);
            return Optional.empty();
        }
    }

    private Optional<String> fetchCustomerUserName(String userId) {
        WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);
        try {
            RemoteApiResponse<CustomerUserProfileResponse> response = businessClient.get()
                .uri("/hrm/customers/by-customer-user/{userId}", userId)
                .retrieve()
                .bodyToMono(CUSTOMER_RESPONSE_TYPE)
                .block(REQUEST_TIMEOUT);

            if (response == null || !response.isSuccess() || response.getData() == null) {
                log.warn("[GW][UserProfile] 고객 사용자 이름 조회 실패 - userId: {}, response: {}", userId, response);
                return Optional.empty();
            }

            return Optional.ofNullable(response.getData().managerName());
        } catch (WebClientResponseException ex) {
            log.error("[GW][UserProfile] 고객 사용자 이름 조회 중 원격 응답 예외 - userId: {}, status: {}, body: {}",
                userId, ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
            return Optional.empty();
        } catch (Exception ex) {
            log.error("[GW][UserProfile] 고객 사용자 이름 조회 중 예기치 못한 오류 - userId: {}", userId, ex);
            return Optional.empty();
        }
    }

    private Optional<String> fetchSupplierUserName(String userId) {
        WebClient scmClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);
        try {
            RemoteApiResponse<SupplierUserProfileResponse> response = scmClient.get()
                .uri("/api/scm-pp/mm/supplier/users/{userId}", userId)
                .retrieve()
                .bodyToMono(SUPPLIER_RESPONSE_TYPE)
                .block(REQUEST_TIMEOUT);

            if (response == null || !response.isSuccess() || response.getData() == null) {
                log.warn("[GW][UserProfile] 공급사 사용자 이름 조회 실패 - userId: {}, response: {}", userId, response);
                return Optional.empty();
            }

            return Optional.ofNullable(response.getData().supplierUserName());
        } catch (WebClientResponseException ex) {
            log.error("[GW][UserProfile] 공급사 사용자 이름 조회 중 원격 응답 예외 - userId: {}, status: {}, body: {}",
                userId, ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
            return Optional.empty();
        } catch (Exception ex) {
            log.error("[GW][UserProfile] 공급사 사용자 이름 조회 중 예기치 못한 오류 - userId: {}", userId, ex);
            return Optional.empty();
        }
    }
}

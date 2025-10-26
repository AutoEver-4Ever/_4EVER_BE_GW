package org.ever._4ever_be_gw.alarm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.alarm.dto.request.AlarmServerRequestDto;
import org.ever._4ever_be_gw.alarm.dto.response.AlarmServerResponseDto;
import org.ever._4ever_be_gw.alarm.service.AlarmHttpService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlarmHttpServiceImpl implements AlarmHttpService {

    @Qualifier("alarmWebClient")
    private final WebClient alarmWebClient;

    @Override
    public Mono<AlarmServerResponseDto.NotificationListResponse> getNotificationList(
        AlarmServerRequestDto.NotificationListRequest request) {
        log.debug("알림 목록 조회 요청 - sortBy: {}, order: {}, source: {}, page: {}, size: {}",
            request.getSortBy(), request.getOrder(), request.getSource(), request.getPage(),
            request.getSize());

        return alarmWebClient.get()
            .uri(uriBuilder -> uriBuilder.path("/notifications/list/{userId}")
                .queryParamIfPresent("sortBy", java.util.Optional.ofNullable(request.getSortBy()))
                .queryParamIfPresent("order", java.util.Optional.ofNullable(request.getOrder()))
                .queryParamIfPresent("source", java.util.Optional.ofNullable(request.getSource()))
                .queryParamIfPresent("page", java.util.Optional.ofNullable(request.getPage()))
                .queryParamIfPresent("size", java.util.Optional.ofNullable(request.getSize()))
                .build(request.getUserId())
            )
            .retrieve()
            .bodyToMono(AlarmServerResponseDto.NotificationListResponse.class)
            .doOnSuccess(response -> log.debug("알림 목록 조회 성공 - 총 {}개",
                response.getItems() != null ? response.getItems().size() : 0))
            .doOnError(error -> log.error("알림 목록 조회 실패", error))
            .onErrorResume(WebClientResponseException.class, ex -> {
                log.error(
                    "알림 서버 응답 오류 - Status: {}, Body: {}",
                    ex.getStatusCode(),
                    ex.getResponseBodyAsString()
                );
                return Mono.error(
                    new RuntimeException("알림 서버에서 목록 조회 중 오류가 발생했습니다: " + ex.getMessage()));
            });
    }

    @Override
    public Mono<AlarmServerResponseDto.NotificationCountResponse> getNotificationCount(
        AlarmServerRequestDto.NotificationCountRequest request) {
        log.debug("알림 갯수 조회 요청 - status: {}", request.getStatus());

        return alarmWebClient.get()
            .uri(uriBuilder -> uriBuilder.path("/notifications/count/{userId}")
                .queryParamIfPresent("status", java.util.Optional.ofNullable(request.getStatus()))
                .build(request.getUserId())
            )
            .retrieve()
            .bodyToMono(AlarmServerResponseDto.NotificationCountResponse.class)
            .doOnSuccess(response -> log.debug("알림 갯수 조회 성공 - count: {}", response.getCount()))
            .doOnError(error -> log.error("알림 갯수 조회 실패", error))
            .onErrorResume(WebClientResponseException.class, ex -> {
                log.error(
                    "알림 서버 응답 오류 - Status: {}, Body: {}",
                    ex.getStatusCode(),
                    ex.getResponseBodyAsString()
                );
                return Mono.error(
                    new RuntimeException("알림 서버에서 갯수 조회 중 오류가 발생했습니다: " + ex.getMessage()));
            });
    }

    @Override
    public Mono<AlarmServerResponseDto.NotificationMarkReadResponse> markReadList(
        AlarmServerRequestDto.NotificationMarkReadRequest request) {
        log.debug("알림 읽음 처리 요청 - notificationIds: {}", request.getNotificationIds());

        return alarmWebClient.patch()
            .uri("/notifications/list/read")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(AlarmServerResponseDto.NotificationMarkReadResponse.class)
            .doOnSuccess(
                response -> log.debug("알림 읽음 처리 성공 - processedCount: {}",
                    response.getProcessedCount()))
            .doOnError(error -> log.error("알림 읽음 처리 실패", error))
            .onErrorResume(WebClientResponseException.class, ex -> {
                log.error("알림 서버 응답 오류 - Status: {}, Body: {}", ex.getStatusCode(),
                    ex.getResponseBodyAsString());
                return Mono.error(
                    new RuntimeException("알림 서버에서 읽음 처리 중 오류가 발생했습니다: " + ex.getMessage()));
            });
    }

    @Override
    public Mono<AlarmServerResponseDto.NotificationMarkReadResponse> markReadAll() {
        log.debug("전체 알림 읽음 처리 요청");

        return alarmWebClient.patch()
            .uri("/notifications/all/read")
            .retrieve()
            .bodyToMono(AlarmServerResponseDto.NotificationMarkReadResponse.class)
            .doOnSuccess(
                response -> log.debug("전체 알림 읽음 처리 성공 - processedCount: {}",
                    response.getProcessedCount()))
            .doOnError(error -> log.error("전체 알림 읽음 처리 실패", error))
            .onErrorResume(WebClientResponseException.class, ex -> {
                log.error("알림 서버 응답 오류 - Status: {}, Body: {}", ex.getStatusCode(),
                    ex.getResponseBodyAsString());
                return Mono.error(
                    new RuntimeException("알림 서버에서 전체 읽음 처리 중 오류가 발생했습니다: " + ex.getMessage()));
            });
    }

    @Override
    public Mono<AlarmServerResponseDto.NotificationMarkReadResponse> markReadOne(
        AlarmServerRequestDto.NotificationMarkReadOneRequest request) {
        log.debug("단일 알림 읽음 처리 요청 - notificationId: {}", request.getNotificationId());

        return alarmWebClient.patch()
            .uri("/notifications/{notificationId}/read", request.getNotificationId())
            .retrieve()
            .bodyToMono(AlarmServerResponseDto.NotificationMarkReadResponse.class)
            .doOnSuccess(
                response -> log.debug("단일 알림 읽음 처리 성공 - processedCount: {}",
                    response.getProcessedCount()))
            .doOnError(error -> log.error("단일 알림 읽음 처리 실패", error))
            .onErrorResume(WebClientResponseException.class, ex -> {
                log.error("알림 서버 응답 오류 - Status: {}, Body: {}", ex.getStatusCode(),
                    ex.getResponseBodyAsString());
                return Mono.error(
                    new RuntimeException("알림 서버에서 단일 읽음 처리 중 오류가 발생했습니다: " + ex.getMessage()));
            });
    }
}

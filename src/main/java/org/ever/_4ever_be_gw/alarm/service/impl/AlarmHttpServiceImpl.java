package org.ever._4ever_be_gw.alarm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.alarm.dto.request.AlarmServerRequestDto;
import org.ever._4ever_be_gw.alarm.dto.response.AlarmServerResponseDto;
import org.ever._4ever_be_gw.alarm.dto.response.NotificationCountResponseDto;
import org.ever._4ever_be_gw.alarm.dto.response.NotificationListResponseDto;
import org.ever._4ever_be_gw.alarm.dto.response.NotificationReadResponseDto;
import org.ever._4ever_be_gw.alarm.service.AlarmHttpService;
import org.ever._4ever_be_gw.alarm.util.AlarmDtoConverter;
import org.ever._4ever_be_gw.common.dto.pagable.PageResponseDto;
import org.ever._4ever_be_gw.common.response.ApiResponse;
import org.ever._4ever_be_gw.config.webclient.ApiClientKey;
import org.ever._4ever_be_gw.config.webclient.WebClientProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlarmHttpServiceImpl implements AlarmHttpService {

    private final WebClientProvider webClientProvider;

    @Override
    public ResponseEntity<ApiResponse<PageResponseDto<NotificationListResponseDto>>> getNotificationList(
        AlarmServerRequestDto.NotificationListRequest request) {
        log.debug("알림 목록 조회 요청 - userId: {}, sortBy: {}, order: {}, source: {}, page: {}, size: {}",
            request.getUserId(), request.getSortBy(), request.getOrder(), request.getSource(),
            request.getPage(),
            request.getSize());

        try {
            WebClient alarmWebClient = webClientProvider.getWebClient(ApiClientKey.ALARM);

            AlarmServerResponseDto.NotificationListResponse serverResponse = alarmWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/notifications/list/{userId}")
                    .queryParamIfPresent("sortBy",
                        java.util.Optional.ofNullable(request.getSortBy()))
                    .queryParamIfPresent("order", java.util.Optional.ofNullable(request.getOrder()))
                    .queryParamIfPresent("source",
                        java.util.Optional.ofNullable(request.getSource()))
                    .queryParamIfPresent("page", java.util.Optional.ofNullable(request.getPage()))
                    .queryParamIfPresent("size", java.util.Optional.ofNullable(request.getSize()))
                    .build(request.getUserId())
                )
                .retrieve()
                .bodyToMono(AlarmServerResponseDto.NotificationListResponse.class)
                .block();

            PageResponseDto<NotificationListResponseDto> clientResponse =
                AlarmDtoConverter.toClientResponse(serverResponse);

            log.info("알림 목록 조회 성공 - 총 {}개",
                serverResponse.getItems() != null ? serverResponse.getItems().size() : 0);
            return ResponseEntity.ok(
                ApiResponse.success(clientResponse, "알림 목록을 성공적으로 조회했습니다.", HttpStatus.OK)
            );

        } catch (WebClientResponseException ex) {
            handleWebClientError("알림 목록 조회", ex);
            HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
            return ResponseEntity.status(status).body(
                ApiResponse.fail("알림 목록 조회 중 오류가 발생했습니다.", status, null)
            );
        } catch (Exception e) {
            log.error("알림 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.fail("알림 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<NotificationCountResponseDto>> getNotificationCount(
        AlarmServerRequestDto.NotificationCountRequest request) {
        log.debug("알림 갯수 조회 요청 - userId: {}, status: {}", request.getUserId(), request.getStatus());

        try {
            WebClient alarmWebClient = webClientProvider.getWebClient(ApiClientKey.ALARM);

            AlarmServerResponseDto.NotificationCountResponse serverResponse = alarmWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/notifications/count/{userId}")
                    .queryParamIfPresent("status",
                        java.util.Optional.ofNullable(request.getStatus()))
                    .build(request.getUserId())
                )
                .retrieve()
                .bodyToMono(AlarmServerResponseDto.NotificationCountResponse.class)
                .block();

            NotificationCountResponseDto clientResponse = AlarmDtoConverter.toClientResponse(
                serverResponse);

            log.info("알림 갯수 조회 성공 - count: {}", serverResponse.getCount());
            return ResponseEntity.ok(
                ApiResponse.success(clientResponse, "알림 갯수를 성공적으로 조회했습니다.", HttpStatus.OK)
            );

        } catch (WebClientResponseException ex) {
            handleWebClientError("알림 갯수 조회", ex);
            HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
            return ResponseEntity.status(status).body(
                ApiResponse.fail("알림 갯수 조회 중 오류가 발생했습니다.", status, null)
            );
        } catch (Exception e) {
            log.error("알림 갯수 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.fail("알림 갯수 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<NotificationReadResponseDto>> markReadList(
        AlarmServerRequestDto.NotificationMarkReadRequest request) {
        log.debug("알림 읽음 처리 요청 - userId: {}, notificationIds: {}", request.getUserId(),
            request.getNotificationIds());

        try {
            WebClient alarmWebClient = webClientProvider.getWebClient(ApiClientKey.ALARM);

            AlarmServerResponseDto.NotificationMarkReadResponse serverResponse = alarmWebClient.patch()
                .uri("/notifications/list/read")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AlarmServerResponseDto.NotificationMarkReadResponse.class)
                .block();

            NotificationReadResponseDto clientResponse = AlarmDtoConverter.toClientResponse(
                serverResponse);

            log.info("알림 읽음 처리 성공 - processedCount: {}", serverResponse.getProcessedCount());
            String msg = (long) request.getNotificationIds().size() + "개의 알림을 성공적으로 읽음 처리했습니다.";
            return ResponseEntity.ok(
                ApiResponse.success(clientResponse, msg, HttpStatus.OK)
            );

        } catch (WebClientResponseException ex) {
            handleWebClientError("알림 읽음 처리", ex);
            HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
            return ResponseEntity.status(status).body(
                ApiResponse.fail("알림 읽음 처리 중 오류가 발생했습니다.", status, null)
            );
        } catch (Exception e) {
            log.error("알림 읽음 처리 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.fail("알림 읽음 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<NotificationReadResponseDto>> markReadAll(
        AlarmServerRequestDto.NotificationMarkReadRequest request
    ) {
        log.debug("전체 알림 읽음 처리 요청");

        AlarmServerRequestDto.NotificationMarkReadAllRequest req = AlarmServerRequestDto.NotificationMarkReadAllRequest.builder()
            .userId(request.getUserId())
            .build();

        try {
            WebClient alarmWebClient = webClientProvider.getWebClient(ApiClientKey.ALARM);

            AlarmServerResponseDto.NotificationMarkReadResponse serverResponse = alarmWebClient.patch()
                .uri("/notifications/all/read")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(AlarmServerResponseDto.NotificationMarkReadResponse.class)
                .block();

            NotificationReadResponseDto clientResponse = AlarmDtoConverter.toClientResponse(
                serverResponse);

            log.info("전체 알림 읽음 처리 성공 - processedCount: {}", serverResponse.getProcessedCount());
            return ResponseEntity.ok(
                ApiResponse.success(clientResponse, "모든 알림을 성공적으로 읽음 처리했습니다.", HttpStatus.OK)
            );

        } catch (WebClientResponseException ex) {
            handleWebClientError("전체 알림 읽음 처리", ex);
            HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
            return ResponseEntity.status(status).body(
                ApiResponse.fail("전체 알림 읽음 처리 중 오류가 발생했습니다.", status, null)
            );
        } catch (Exception e) {
            log.error("전체 알림 읽음 처리 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.fail("전체 알림 읽음 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR,
                    null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> markReadOne(
        AlarmServerRequestDto.NotificationMarkReadOneRequest request) {
        log.debug("단일 알림 읽음 처리 요청 - userId: {}, notificationId: {}", request.getUserId(),
            request.getNotificationId());

        AlarmServerRequestDto.NotificationMarkReadAllRequest req = AlarmServerRequestDto.NotificationMarkReadAllRequest.builder()
            .userId(request.getUserId())
            .build();

        try {
            WebClient alarmWebClient = webClientProvider.getWebClient(ApiClientKey.ALARM);

            alarmWebClient.patch()
                .uri("/notifications/{notificationId}/read", request.getNotificationId())
                .bodyValue(req)
                .retrieve()
                .bodyToMono(AlarmServerResponseDto.NotificationMarkReadResponse.class)
                .block();

            log.info("단일 알림 읽음 처리 성공");
            return ResponseEntity.ok(
                ApiResponse.success(null, "알림을 성공적으로 읽음 처리했습니다.", HttpStatus.OK)
            );

        } catch (WebClientResponseException ex) {
            handleWebClientError("단일 알림 읽음 처리", ex);
            HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
            return ResponseEntity.status(status).body(
                ApiResponse.fail("단일 알림 읽음 처리 중 오류가 발생했습니다.", status, null)
            );
        } catch (Exception e) {
            log.error("단일 알림 읽음 처리 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.fail("단일 알림 읽음 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR,
                    null)
            );
        }
    }

    /**
     * WebClient 오류를 처리하고 로깅하는 공통 메서드
     * 400, 500번대 에러에 대한 상세 처리
     */
    private void handleWebClientError(String operation, WebClientResponseException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String errorBody = ex.getResponseBodyAsString();

        if (status == HttpStatus.BAD_REQUEST) {
            log.error("{} 실패 - 400 Bad Request. Status: {}, Body: {}", operation,
                ex.getStatusCode(),
                errorBody);
        } else if (status == HttpStatus.UNAUTHORIZED) {
            log.error("{} 실패 - 401 Unauthorized. Status: {}, Body: {}", operation,
                ex.getStatusCode(),
                errorBody);
        } else if (status == HttpStatus.FORBIDDEN) {
            log.error("{} 실패 - 403 Forbidden. Status: {}, Body: {}", operation, ex.getStatusCode(),
                errorBody);
        } else if (status == HttpStatus.NOT_FOUND) {
            log.error("{} 실패 - 404 Not Found. Status: {}, Body: {}", operation, ex.getStatusCode(),
                errorBody);
        } else if (status.is4xxClientError()) {
            log.error("{} 실패 - 4xx Client Error. Status: {}, Body: {}", operation,
                ex.getStatusCode(),
                errorBody);
        } else if (status.is5xxServerError()) {
            log.error("{} 실패 - 5xx Server Error. Status: {}, Body: {}", operation,
                ex.getStatusCode(),
                errorBody);
        } else {
            log.error("{} 실패 - 기타 오류. Status: {}, Body: {}", operation, ex.getStatusCode(),
                errorBody);
        }
    }
}

package org.ever._4ever_be_gw.alarm.service;

import org.ever._4ever_be_gw.alarm.dto.request.AlarmServerRequestDto;
import org.ever._4ever_be_gw.alarm.dto.response.AlarmServerResponseDto;
import reactor.core.publisher.Mono;

public interface AlarmHttpService {

    /**
     * 알림 목록 조회
     */
    Mono<AlarmServerResponseDto.NotificationListResponse> getNotificationList(
        AlarmServerRequestDto.NotificationListRequest request
    );

    /**
     * 알림 갯수 조회
     */
    Mono<AlarmServerResponseDto.NotificationCountResponse> getNotificationCount(
        AlarmServerRequestDto.NotificationCountRequest request
    );

    /**
     * 알림 읽음 처리 (목록)
     */
    Mono<AlarmServerResponseDto.NotificationMarkReadResponse> markReadList(
        AlarmServerRequestDto.NotificationMarkReadRequest request
    );

    /**
     * 알림 읽음 처리 (전체)
     */
    Mono<AlarmServerResponseDto.NotificationMarkReadResponse> markReadAll();

    /**
     * 알림 읽음 처리 (단일)
     */
    Mono<AlarmServerResponseDto.NotificationMarkReadResponse> markReadOne(
        AlarmServerRequestDto.NotificationMarkReadOneRequest request
    );
}

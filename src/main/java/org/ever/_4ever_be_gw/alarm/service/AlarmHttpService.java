package org.ever._4ever_be_gw.alarm.service;

import org.ever._4ever_be_gw.alarm.dto.request.AlarmServerRequestDto;
import org.ever._4ever_be_gw.alarm.dto.response.NotificationCountResponseDto;
import org.ever._4ever_be_gw.alarm.dto.response.NotificationListResponseDto;
import org.ever._4ever_be_gw.alarm.dto.response.NotificationReadResponseDto;
import org.ever._4ever_be_gw.common.dto.pagable.PageResponseDto;
import org.ever._4ever_be_gw.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface AlarmHttpService {

    /**
     * 알림 목록 조회
     */
    ResponseEntity<ApiResponse<PageResponseDto<NotificationListResponseDto>>> getNotificationList(
        AlarmServerRequestDto.NotificationListRequest request
    );

    /**
     * 알림 갯수 조회
     */
    ResponseEntity<ApiResponse<NotificationCountResponseDto>> getNotificationCount(
        AlarmServerRequestDto.NotificationCountRequest request
    );

    /**
     * 알림 읽음 처리 (목록)
     */
    ResponseEntity<ApiResponse<NotificationReadResponseDto>> markReadList(
        AlarmServerRequestDto.NotificationMarkReadRequest request
    );

    /**
     * 알림 읽음 처리 (전체)
     */
    ResponseEntity<ApiResponse<NotificationReadResponseDto>> markReadAll(
        AlarmServerRequestDto.NotificationMarkReadRequest request
    );

    /**
     * 알림 읽음 처리 (단일)
     */
    ResponseEntity<ApiResponse<Void>> markReadOne(
        AlarmServerRequestDto.NotificationMarkReadOneRequest request
    );

    /**
     * FCM 토큰 등록
     */
    ResponseEntity<ApiResponse<Void>> registerFcmToken(
        AlarmServerRequestDto.NotificationFcmTokenRequest request
    );
}

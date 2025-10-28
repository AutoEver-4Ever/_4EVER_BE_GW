package org.ever._4ever_be_gw.alarm.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface AlarmSendService {

    // 사용자별 SseEmitter 추가
    SseEmitter addEmitter(String userId);

    // 사용자별 SseEmitter 제거
    void removeEmitter(String userId);

    // 사용자별 알림 메시지 전송
    void sendAlarmMessage(String userId, String message, Object data);
}

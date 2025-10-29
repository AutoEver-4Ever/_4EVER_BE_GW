package org.ever._4ever_be_gw.alarm.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.alarm.service.AlarmSendService;
import org.ever.event.AlarmSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlarmSendServiceImpl implements AlarmSendService {

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60; // 1 hour

    private final Map<String, SseEmitter> emitterMap = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    @Override
    public SseEmitter addEmitter(String userId) {
        log.info("SSE Emitter 추가 - UserId: {}", userId);

        // 이미 존재하는 Emitter가 있다면 제거
        SseEmitter existingEmitter = emitterMap.remove(userId);
        if (existingEmitter != null) {
            log.info("기존 SSE Emitter 제거 - UserId: {}", userId);
            try {
                existingEmitter.completeWithError(new Exception("새로운 연결로 교체됨"));
            } catch (Exception e) {
                log.warn("기존 SSE Emitter 종료 중 오류 - UserId: {}", userId, e);
            }
        }

        // 새로운 Emitter 생성
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

        // 완료 콜백 등록
        emitter.onCompletion(() -> {
            log.info("SSE Emitter 완료 - UserId: {}", userId);
            emitterMap.remove(userId);
        });

        // 타임아웃 콜백 등록
        emitter.onTimeout(() -> {
            log.warn("SSE Emitter 타임아웃 - UserId: {}", userId);
            emitterMap.remove(userId);
            try {
                emitter.completeWithError(new Exception("타임아웃"));
            } catch (Exception e) {
                log.warn("SSE Emitter 타임아웃 처리 중 오류 - UserId: {}", userId, e);
            }
        });

        // 에러 콜백 등록
        emitter.onError((ex) -> {
            log.error("SSE Emitter 에러 - UserId: {}", userId, ex);
            emitterMap.remove(userId);
        });

        // Emitter 저장
        emitterMap.put(userId, emitter);
        log.info("SSE Emitter 추가 완료 - UserId: {}, 현재 연결 수: {}", userId, emitterMap.size());

        return emitter;
    }

    @Override
    public void removeEmitter(String userId) {
        log.info("SSE Emitter 제거 - UserId: {}", userId);
        SseEmitter emitter = emitterMap.remove(userId);
        if (emitter != null) {
            try {
                emitter.complete();
            } catch (Exception e) {
                log.warn("SSE Emitter 종료 중 오류 - UserId: {}", userId, e);
            }
        }
    }

    @Override
    public void sendAlarmMessage(AlarmSentEvent event) {
        log.info("알림 메시지 전송 시작 - AlarmId: {}, Message: {}", event.getAlarmId(), event.getMessage());

        SseEmitter emitter = emitterMap.get(event.getTargetId());
        if (emitter == null) {
            log.warn("SSE Emitter를 찾을 수 없음 - targetId: {}", event.getTargetId());
            return;
        }

        try {
            // 데이터를 JSON으로 변환
            String jsonData = objectMapper.writeValueAsString(
                event.getTitle() + ": " + event.getMessage());

            // SSE 메시지 전송
            SseEmitter.SseEventBuilder sseEvent = SseEmitter.event()
                .name("alarm")
                .data(jsonData);

            emitter.send(sseEvent);
            log.info("알림 메시지 전송 성공 - targetId: {}", event.getTargetId());

        } catch (IOException e) {
            log.error("알림 메시지 전송 실패 - targetId: {}", event.getTargetId(), e);
            emitterMap.remove(event.getTargetId());
            try {
                emitter.completeWithError(e);
            } catch (Exception ex) {
                log.warn("SSE Emitter 에러 처리 중 오류 - targetId: {}", event.getTargetId(), e);
            }
        }
    }
}

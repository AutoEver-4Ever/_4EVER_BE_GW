package org.ever._4ever_be_gw.alarm.controller;

import com.github.f4b6a3.uuid.UuidCreator;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.alarm.dto.request.AlarmServerRequestDto;
import org.ever._4ever_be_gw.alarm.dto.request.NotificationFcmTokenRequestDto;
import org.ever._4ever_be_gw.alarm.service.AlarmHttpService;
import org.ever._4ever_be_gw.alarm.util.AlarmDtoConverter;
import org.ever._4ever_be_gw.common.response.ApiResponse;
import org.ever._4ever_be_gw.config.security.principal.EverJwtAuthenticationToken;
import org.ever._4ever_be_gw.config.security.principal.EverUserPrincipal;
import org.ever._4ever_be_gw.infrastructure.kafka.producer.KafkaProducerService;
import org.ever.event.AlarmEvent;
import org.ever.event.alarm.AlarmType;
import org.ever.event.alarm.LinkType;
import org.ever.event.alarm.TargetType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/alarm/fcm-tokens")
@Validated
@RequiredArgsConstructor
@Slf4j
public class FcmTokenController {

    private final AlarmHttpService alarmHttpService;


    // ===== FCM 토큰 등록 =====
    @PostMapping("/register")
    @Operation(summary = "FCM 토큰 등록", description = "사용자의 FCM 토큰을 등록합니다.")
    public ResponseEntity<ApiResponse<Void>> registerFcmToken(
        @AuthenticationPrincipal EverUserPrincipal principal,
        EverJwtAuthenticationToken authentication,

        @Valid
        @RequestBody
        NotificationFcmTokenRequestDto notificationFcmTokenRequestDto
    ) {
        final String token = (authentication != null && authentication.getToken() != null)
            ? authentication.getToken().getTokenValue()
            : null;

        log.info("[FCM][USER] 사용자 정보 조회 요청 수신: principalPresent={}, tokenPresent={}",
            principal != null, token != null);

        UUID userId = UUID.fromString(principal.getUserId());

        AlarmServerRequestDto.NotificationFcmTokenRequest request = AlarmDtoConverter.toFcmTokenServerRequest(
            userId,
            notificationFcmTokenRequestDto.getToken(),
            notificationFcmTokenRequestDto.getDeviceId(),
            notificationFcmTokenRequestDto.getDeviceType()
        );

        // Service가 ResponseEntity를 반환하므로 그대로 반환
        return alarmHttpService.registerFcmToken(request);
    }

}

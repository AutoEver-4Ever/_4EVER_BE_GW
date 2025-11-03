package org.ever._4ever_be_gw.alarm.controller;

import com.github.f4b6a3.uuid.UuidCreator;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.alarm.dto.request.AlarmServerRequestDto;
import org.ever._4ever_be_gw.alarm.dto.request.NotificationFcmTokenRequestDto;
import org.ever._4ever_be_gw.alarm.service.AlarmHttpService;
import org.ever._4ever_be_gw.alarm.util.AlarmDtoConverter;
import org.ever._4ever_be_gw.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
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

    private final UUID tempUuid = UuidCreator.getTimeOrderedEpoch(); // 임시 사용자 UUID // TODO : 인증 연동 후 수정

    private final AlarmHttpService alarmHttpService;


    // ===== FCM 토큰 등록 =====
    @PostMapping("/register")
    @Operation(summary = "FCM 토큰 등록", description = "사용자의 FCM 토큰을 등록합니다.")
    public ResponseEntity<ApiResponse<Void>> registerFcmToken(
        @Valid
        @RequestBody
        NotificationFcmTokenRequestDto notificationFcmTokenRequestDto
    ) {
        AlarmServerRequestDto.NotificationFcmTokenRequest request = AlarmDtoConverter.toFcmTokenServerRequest(
            tempUuid,
            notificationFcmTokenRequestDto.getToken(),
            notificationFcmTokenRequestDto.getDeviceId(),
            notificationFcmTokenRequestDto.getDeviceType()
        );

        // Service가 ResponseEntity를 반환하므로 그대로 반환
        return alarmHttpService.registerFcmToken(request);
    }

}

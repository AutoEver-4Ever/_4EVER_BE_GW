package org.ever._4ever_be_gw.alarm.controller;

import com.github.f4b6a3.uuid.UuidCreator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.Collections;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.alarm.dto.request.AlarmServerRequestDto;
import org.ever._4ever_be_gw.alarm.dto.request.NotificationMarkReadRequestDto;
import org.ever._4ever_be_gw.alarm.dto.response.NotificationCountResponseDto;
import org.ever._4ever_be_gw.alarm.dto.response.NotificationListResponseDto;
import org.ever._4ever_be_gw.alarm.dto.response.NotificationReadResponseDto;
import org.ever._4ever_be_gw.alarm.service.AlarmHttpService;
import org.ever._4ever_be_gw.alarm.service.AlarmSendService;
import org.ever._4ever_be_gw.alarm.util.AlarmDtoConverter;
import org.ever._4ever_be_gw.common.dto.pagable.PageResponseDto;
import org.ever._4ever_be_gw.common.dto.validation.AllowedValues;
import org.ever._4ever_be_gw.common.dto.validation.ValidUuidV7;
import org.ever._4ever_be_gw.common.response.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/alarm/notifications")
@Validated
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", allowCredentials = "false")
@Tag(name = "알림", description = "알림(Notification) API")
public class AlarmController {

    final static int MAX_PAGE_SIZE = 100;

    private final AlarmHttpService alarmHttpService;
    private final AlarmSendService alarmSendService;
    private final UUID tempUuid = UuidCreator.getTimeOrderedEpoch(); // 임시 사용자 UUID // TODO : 인증 연동 후 수정


    // ===== 알림 목록 조회 =====
    @GetMapping("/list")
    @Operation(summary = "알림 목록 조회", description = "알림 목록을 페이징/정렬/필터와 함께 조회합니다.")
    public ResponseEntity<ApiResponse<PageResponseDto<NotificationListResponseDto>>> getNotificationList(
        @AllowedValues(allowedValues = {
            "createdAt"}, ignoreCase = true, message = "sortBy는 createdAt만 허용됩니다.")
        @RequestParam(name = "sortBy", required = false, defaultValue = "createdAt")
        String sortBy,
        @AllowedValues(allowedValues = {"asc",
            "desc"}, ignoreCase = true, message = "order는 asc 또는 desc만 허용됩니다.")
        @RequestParam(name = "order", required = false, defaultValue = "desc")
        String order,
        @AllowedValues(allowedValues = {"PR", "SD", "IM", "FCM", "HRM", "PP", "CUS",
            "SUP"}, ignoreCase = true, message = "유효하지 않은 source 값입니다.")
        @RequestParam(name = "source", required = false)
        String source,
        @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다.")
        @RequestParam(name = "page", required = false, defaultValue = "0")
        Integer page,
        @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
        @Max(value = MAX_PAGE_SIZE, message = "페이지 크기는 최대 " + MAX_PAGE_SIZE + "까지 가능합니다.")
        @RequestParam(name = "size", required = false, defaultValue = "20")
        Integer size
    ) {

        AlarmServerRequestDto.NotificationListRequest request = AlarmDtoConverter.toServerRequest(
            tempUuid, sortBy, order, source, page, size
        );

        // Service가 ResponseEntity를 반환하므로 그대로 반환
        return alarmHttpService.getNotificationList(request);
    }

    // ===== 알림 갯수 조회 =====
    @GetMapping("/count")
    @Operation(summary = "알림 갯수 조회", description = "상태별(READ/UNREAD) 알림 갯수를 조회합니다.")
    public ResponseEntity<ApiResponse<NotificationCountResponseDto>> getNotificationCount(
        @AllowedValues(allowedValues = {"READ",
            "UNREAD"}, ignoreCase = true, message = "유효하지 않은 status 값입니다. 허용값: READ, UNREAD")
        @RequestParam(name = "status", required = false)
        String status
    ) {
        AlarmServerRequestDto.NotificationCountRequest request = AlarmDtoConverter.toCountServerRequest(
            tempUuid, status
        );

        // Service가 ResponseEntity를 반환하므로 그대로 반환
        return alarmHttpService.getNotificationCount(request);
    }

    // ===== 알림 구독 요청 =====
    @GetMapping(value = "/subscribe/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "알림 구독 요청", description = "SSE를 통해 실시간 알림을 구독합니다.")
    public SseEmitter subscribe(
        @ValidUuidV7
        @PathVariable("userId")
        String userId,
        HttpServletRequest request
    ) {
        log.info("[SSE][SUBSCRIBE-REQUEST] userId={}, remoteAddr={}",
            userId, request.getRemoteAddr());

        // SSE Emitter 추가 후 그대로 반환 (연결 유지)
        SseEmitter emitter = alarmSendService.addEmitter(userId);

        log.info(
            "[SSE][SUBSCRIBE-SUCCESS] userId={}, emitterHash={}",
            userId, System.identityHashCode(emitter));

        return emitter;
    }

    // ===== 알림 읽음 처리 (목록) =====
    @PatchMapping("/list/read")
    @Operation(summary = "알림 읽음 처리(목록)", description = "주어진 알림 ID 목록을 읽음 처리합니다.")
    public ResponseEntity<ApiResponse<NotificationReadResponseDto>> markReadList(
        @Valid
        @RequestBody
        NotificationMarkReadRequestDto notificationMarkReadRequestDto
    ) {

        AlarmServerRequestDto.NotificationMarkReadRequest request = AlarmDtoConverter.toMarkReadServerRequest(
            tempUuid, notificationMarkReadRequestDto.getNotificationId()
        );

        // Service가 ResponseEntity를 반환하므로 그대로 반환
        return alarmHttpService.markReadList(request);
    }

    // ===== 알림 읽음 처리 (전체) =====
    @PatchMapping("/all/read")
    @Operation(summary = "알림 읽음 처리(전체)", description = "모든 알림을 읽음 처리합니다.")
    public ResponseEntity<ApiResponse<NotificationReadResponseDto>> markReadAll() {
        AlarmServerRequestDto.NotificationMarkReadRequest request = AlarmDtoConverter.toMarkReadServerRequest(
            tempUuid, Collections.emptyList()
        );

        // Service가 ResponseEntity를 반환하므로 그대로 반환
        return alarmHttpService.markReadAll(
            request
        );
    }

    // ===== 알림 읽음 처리 (단일) =====
    @PatchMapping("/{notificationId}/read")
    @Operation(summary = "알림 읽음 처리(단일)", description = "특정 알림을 읽음 처리합니다.")
    public ResponseEntity<ApiResponse<Void>> markReadOne(
        @PathVariable("notificationId")
        @ValidUuidV7
        String notificationId
    ) {
        AlarmServerRequestDto.NotificationMarkReadOneRequest request =
            AlarmDtoConverter.toMarkReadOneServerRequest(tempUuid, notificationId);

        // Service가 ResponseEntity를 반환하므로 그대로 반환
        return alarmHttpService.markReadOne(request);
    }

}
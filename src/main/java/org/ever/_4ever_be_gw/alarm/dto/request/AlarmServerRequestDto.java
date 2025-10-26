package org.ever._4ever_be_gw.alarm.dto.request;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AlarmServerRequestDto {

    // 알림 목록 조회 요청
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationListRequest {

        private UUID userId;
        private String sortBy;
        private String order;
        private String source;
        private Integer page;
        private Integer size;
    }

    // 알림 갯수 조회 요청
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationCountRequest {

        private UUID userId;
        private String status;
    }

    // 알림 읽음 처리 요청
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationMarkReadRequest {

        private UUID userId;
        private List<String> notificationIds;
    }

    // 알림 읽음 처리 (단일) 요청
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationMarkReadOneRequest {

        private UUID userId;
        private String notificationId;
    }
}
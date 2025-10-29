package org.ever._4ever_be_gw.user.dto;

import java.time.Instant;
public record UserInfoResponse(
    String userId,
    String loginEmail,
    String userRole,
    String userType,
    Instant tokenIssuedAt,
    Instant tokenExpiresAt
) {
}

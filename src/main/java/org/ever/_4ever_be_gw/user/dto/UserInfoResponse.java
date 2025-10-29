package org.ever._4ever_be_gw.user.dto;

import java.time.Instant;
import java.util.List;

public record UserInfoResponse(
    String userId,
    String loginEmail,
    String userRole,
    String userType,
    List<String> authorities,
    Instant tokenIssuedAt,
    Instant tokenExpiresAt
) {
}

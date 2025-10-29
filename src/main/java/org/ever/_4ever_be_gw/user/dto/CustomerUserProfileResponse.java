package org.ever._4ever_be_gw.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CustomerUserProfileResponse(
    String userId,
    @JsonProperty("customerName")
    String managerName
) {
}

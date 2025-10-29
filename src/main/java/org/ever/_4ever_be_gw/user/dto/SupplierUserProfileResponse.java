package org.ever._4ever_be_gw.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SupplierUserProfileResponse(
    String userId,
    @JsonProperty("supplierUserName")
    String supplierUserName
) {
}

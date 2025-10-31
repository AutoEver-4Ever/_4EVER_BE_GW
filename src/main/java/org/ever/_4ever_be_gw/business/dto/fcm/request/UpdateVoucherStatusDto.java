package org.ever._4ever_be_gw.business.dto.fcm.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateVoucherStatusDto {

    @JsonProperty("voucherId")
    private String voucherId;

    @JsonProperty("statusCode")
    private String statusCode;
}

package org.ever._4ever_be_gw.business.dto.fcm.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class APInvoiceItemDto {
    @JsonProperty("itemId")
    private String itemId;

    @JsonProperty("itemName")
    private String itemName;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("unitOfMaterialName")
    private String unitOfMaterialName;

    @JsonProperty("unitPrice")
    private BigDecimal unitPrice;

    @JsonProperty("totalPrice")
    private BigDecimal totalPrice;
}

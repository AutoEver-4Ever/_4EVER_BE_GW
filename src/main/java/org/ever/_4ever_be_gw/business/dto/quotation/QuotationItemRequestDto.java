package org.ever._4ever_be_gw.business.dto.quotation;

import lombok.Getter;

@Getter
public class QuotationItemRequestDto {
    private String itemId; // UUID
    private Integer quantity;
    private Long unitPrice;
}

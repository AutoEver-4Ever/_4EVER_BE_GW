package org.ever._4ever_be_gw.business.dto;

import lombok.Getter;

@Getter
public class QuotationItemRequestDto {
    private Long itemId;
    private Integer quantity;
    private Long unitPrice;
}

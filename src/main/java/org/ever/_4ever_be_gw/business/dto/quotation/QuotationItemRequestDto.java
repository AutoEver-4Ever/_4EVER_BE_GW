package org.ever._4ever_be_gw.business.dto.quotation;

import lombok.Getter;
import java.time.LocalDate;

@Getter
public class QuotationItemRequestDto {
    private String itemId;
    private Integer quantity;
    private Long unitPrice;
    private LocalDate dueDate;
}

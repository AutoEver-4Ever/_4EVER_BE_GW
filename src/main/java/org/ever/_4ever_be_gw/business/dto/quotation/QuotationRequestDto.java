package org.ever._4ever_be_gw.business.dto.quotation;

import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class QuotationRequestDto {
    private LocalDate dueDate;
    private List<QuotationItemRequestDto> items;
    private String note;
}


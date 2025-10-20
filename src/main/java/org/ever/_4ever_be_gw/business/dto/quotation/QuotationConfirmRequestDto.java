package org.ever._4ever_be_gw.business.dto.quotation;

import lombok.Getter;

import java.util.List;

@Getter
public class QuotationConfirmRequestDto {
    private List<Long> quotationIds;
}


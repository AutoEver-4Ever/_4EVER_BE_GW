package org.ever._4ever_be_gw.business.dto.quotation;

import lombok.Getter;

import java.util.List;

@Getter
public class QuotationConfirmRequestDto {
    private String qoId; // 확인 대상 견적 ID (UUID)
}

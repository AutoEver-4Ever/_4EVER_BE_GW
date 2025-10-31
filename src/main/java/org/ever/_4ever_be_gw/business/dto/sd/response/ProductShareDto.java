package org.ever._4ever_be_gw.business.dto.sd.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductShareDto {
    private String productCode;
    private String productName;
    private BigDecimal sale;
    private Integer saleShare; // percentage
}

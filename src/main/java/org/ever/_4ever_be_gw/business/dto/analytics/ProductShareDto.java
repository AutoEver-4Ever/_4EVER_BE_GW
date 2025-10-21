package org.ever._4ever_be_gw.business.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductShareDto {
    private String productCode;
    private String productName;
    private Long sale;
    private Double saleShare;
}


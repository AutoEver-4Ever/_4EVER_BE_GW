package org.ever._4ever_be_gw.business.dto.sd.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCustomerDto {
    private String customerId;
    private String customerName;
    private String customerBaseAddress;
    private String customerDetailAddress;
    private CustomerManagerDto manager;
}

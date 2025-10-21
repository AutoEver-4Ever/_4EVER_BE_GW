package org.ever._4ever_be_gw.business.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDetailDto {
    private String customerId;
    private String customerNumber;
    private String customerName;
    private String ceoName;
    private String businessNumber;
    private String statusCode;

    private String contactPhone;
    private String contactEmail;
    private String address;
    private String detailAddress;

    private CustomerManagerDto manager;

    private Integer totalOrders;
    private Long totalTransactionAmount;
    private String note;
}


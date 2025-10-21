package org.ever._4ever_be_gw.business.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerListItemDto {
    private String customerId;                 // UUID
    private String customerNumber;             // 고객 번호
    private String customerName;               // 고객사명
    private String businessNumber;             // 사업자 등록번호
    private String statusCode;                 // ACTIVE / DEACTIVE

    private String contactPhone;               // 대표 전화
    private String contactEmail;               // 대표 이메일
    private String address;                    // 주소 요약

    private CustomerManagerDto manager;        // 담당자 정보

    private Integer totalOrders;               // 총 주문 건수
    private Long totalTransactionAmount;       // 총 거래 금액
    private String lastOrderDate;              // YYYY-MM-DD
}


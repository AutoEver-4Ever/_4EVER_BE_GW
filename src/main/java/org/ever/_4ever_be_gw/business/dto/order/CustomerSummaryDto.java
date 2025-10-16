package org.ever._4ever_be_gw.business.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSummaryDto {
    private Long customerId;                // 고객사 ID
    private String customerName;            // 고객사 이름
    private String customerCode;            // 고객사 코드
    private String customerBaseAddress;     // 기본주소
    private String customerDetailAddress;   // 상세주소
    private ManagerDto manager;      // { managerName, managerPhone, managerEmail }
}


package org.ever._4ever_be_gw.scmpp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceivedPurchaseOrderDto {
    private String purchaseOrderId;
    private String purchaseOrderNumber;
    private String supplierCompanyName;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime orderDate;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime receivedDate;
    private int totalAmount;
    private String statusCode;
}

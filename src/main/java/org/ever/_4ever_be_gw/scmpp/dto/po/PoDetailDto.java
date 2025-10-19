package org.ever._4ever_be_gw.scmpp.dto.po;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PoDetailDto {
    private Long invoiceId;
    private String invoiceCode;
    private String statusCode;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private Long purchaseOrderId;
    private String purchaseOrderCode;
    private LocalDate orderDate;
    private LocalDate requestedDeliveryDate;
    private Long supplierId;
    private String supplierCode;
    private String supplierName;
    private String managerPhone;
    private String managerEmail;
    private String deliveryAddress;
    private ReferenceInfo reference;
    private List<PoItemDto> items;
    private long totalAmount;
    private String note;
    private Instant createdAt;
    private Instant updatedAt;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReferenceInfo {
        private String type;
        private Long purchaseOrderId;
        private String purchaseOrderCode;
        private LocalDate orderDate;
        private LocalDate requestedDeliveryDate;
    }
}

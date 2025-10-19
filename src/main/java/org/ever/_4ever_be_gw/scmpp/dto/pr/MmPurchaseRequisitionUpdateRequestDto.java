package org.ever._4ever_be_gw.scmpp.dto.pr;

import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class MmPurchaseRequisitionUpdateRequestDto {

    private LocalDate desiredDeliveryDate;
    private List<ItemOperation> items;

    @Getter
    public static class ItemOperation {
        private String op;
        private Long id;
        private Integer lineNo;
        private String itemName;
        private Integer quantity;
        private String uomName;
        private Long expectedUnitPrice;
        private String preferredVendorName;
        private String purpose;
        private String note;
    }
}


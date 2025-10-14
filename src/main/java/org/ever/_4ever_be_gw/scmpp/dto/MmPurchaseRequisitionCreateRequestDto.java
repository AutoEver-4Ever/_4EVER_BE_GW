package org.ever._4ever_be_gw.scmpp.dto;

import lombok.Getter;
import java.time.LocalDate;
import java.util.List;

@Getter
public class MmPurchaseRequisitionCreateRequestDto {
    private Long requesterId;
    private List<Item> items;

    @Getter
    public static class Item {
        private String itemName;
        private Integer quantity;
        private String uomName;
        private Long expectedUnitPrice;
        private Long expectedTotalPrice;
        private String preferredVendorName;
        private LocalDate desiredDeliveryDate;
        private String purpose;
        private String note;
    }
}


package org.ever._4ever_be_gw.business.dto.sd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryCheckItemDto {
    private Long itemId;            // 품목 ID
    private String itemName;        // 품목명
    private int requiredQty;        // 필요 수량
    private int inventoryQty;       // 현재 재고 수량
    private int shortageQty;        // 부족 수량(음수 없음)
    private String statusCode;      // FULFILLED | SHORTAGE
    private boolean productionRequired; // 생산 필요 여부
}


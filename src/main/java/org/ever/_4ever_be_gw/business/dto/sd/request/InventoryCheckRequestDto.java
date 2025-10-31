package org.ever._4ever_be_gw.business.dto.sd.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryCheckRequestDto {
    @JsonProperty("items")
    private List<InventoryCheckItemDto> items;
}

package org.ever._4ever_be_gw.business.dto.sd.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerListResponseDto {
    private List<CustomerListItemDto> customers;
    private PageInfo page;
}

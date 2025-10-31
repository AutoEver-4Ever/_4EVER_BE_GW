package org.ever._4ever_be_gw.mockdata.scmpp.dto.mm.supplier;

import java.util.List;
import lombok.Getter;
import org.ever._4ever_be_gw.business.dto.order.ManagerDto;

@Getter
public class SupplierCreateRequestDto {
    private SupplierInfoDto supplierInfo;      // 공급업체 정보
    private ManagerDto managerInfo;        // 담당자 정보
    private List<MaterialItemsDto> materialList;
}

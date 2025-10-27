package org.ever._4ever_be_gw.scmpp.dto.supplier;

import java.util.List;
import lombok.Getter;
import org.ever._4ever_be_gw.business.dto.order.ManagerDto;
import org.ever._4ever_be_gw.scmpp.dto.mm.MaterialItemsDto;
import org.ever._4ever_be_gw.scmpp.dto.mm.SupplierInfoDto;

@Getter
public class MmSupplierCreateRequestDto {
    private SupplierInfoDto supplierInfo;      // 공급업체 정보
    private ManagerDto managerInfo;        // 담당자 정보
    private List<MaterialItemsDto> materialList;
}

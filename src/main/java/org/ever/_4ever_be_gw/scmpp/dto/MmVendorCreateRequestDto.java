package org.ever._4ever_be_gw.scmpp.dto;

import java.util.List;
import lombok.Getter;

@Getter
public class MmVendorCreateRequestDto {
    private String companyName;
    private String category;
    private String contactPerson;
    private String contactPhone;
    private String email;
    private Integer deliveryLeadTime;
    private String address;
    private List<String> materialList;
}


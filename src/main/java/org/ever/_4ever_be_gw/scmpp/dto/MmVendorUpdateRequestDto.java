package org.ever._4ever_be_gw.scmpp.dto;

import java.util.List;
import lombok.Getter;

@Getter
public class MmVendorUpdateRequestDto {
    private String companyName;
    private String category;
    private String address;
    private Integer leadTimeDays;
    private List<String> materialList;
    private String statusCode;

    private String contactPerson;
    private String contactPhone;
    private String contactEmail;
}


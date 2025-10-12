package org.ever._4ever_be_gw.business.dto;

import lombok.Getter;

@Getter
public class CustomerCreateRequestDto {
    private String companyName;
    private String businessNumber;
    private String ceoName;
    private String contactPhone;
    private String contactEmail;
    private String address;
    private Manager manager;
    private String note;

    @Getter
    public static class Manager {
        private String name;
        private String mobile;
        private String email;
    }
}


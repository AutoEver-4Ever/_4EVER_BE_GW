package org.ever._4ever_be_gw.business.dto;

import lombok.Getter;

@Getter
public class CustomerUpdateRequestDto {
    private String companyName;
    private String ceo;
    private String businessNumber;
    private String status; // 활성/비활성
    private Contact contact;
    private Manager manager;
    private String note;

    @Getter
    public static class Contact {
        private String phone;
        private String address;
        private String email;
    }

    @Getter
    public static class Manager {
        private String name;
        private String mobile;
        private String email;
    }
}


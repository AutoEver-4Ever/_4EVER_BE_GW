package org.ever._4ever_be_gw.business.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerCreateRequestDto {
    private String companyName;     // 고객사명
    private String businessNumber;  // 고객사 사업자 번호
    private String ceoName;         // 고객사 CEO 이름
    private String contactPhone;    // 고객사 전화번호
    private String contactEmail;    // 고객사 이메일
    private String zipCode;         // 우편번호
    private String address;         // 고객사 주소
    private String detailAddress;   // 고객사 상세 주소
    private Manager manager;        // 고객사의 담당자 정보
    private String note;            // 비고

    @Getter
    @Setter
    public static class Manager {
        private String name;        // 담당자 이름
        private String mobile;      // 담당자 전화번호
        private String email;       // 이메일
    }
}

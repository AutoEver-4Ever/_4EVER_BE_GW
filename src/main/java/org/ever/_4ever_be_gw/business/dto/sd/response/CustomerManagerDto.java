package org.ever._4ever_be_gw.business.dto.sd.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerManagerDto {
    private String managerName;   // name -> managerName
    private String managerPhone;  // mobile -> managerPhone
    private String managerEmail;  // email -> managerEmail
}

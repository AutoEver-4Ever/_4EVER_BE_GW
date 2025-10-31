package org.ever._4ever_be_gw.business.dto.employee;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class EmployeeUpdateRequestDto {

    @Size(max = 50, message = "직원 이름은 최대 50자까지 입력 가능합니다")
    private String employeeName;

    private String departmentId;

    @Size(max = 20, message = "직급은 최대 20자까지 입력 가능합니다")
    private String positionId;

}

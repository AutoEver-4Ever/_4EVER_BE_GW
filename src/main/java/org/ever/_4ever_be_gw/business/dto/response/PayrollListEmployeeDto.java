package org.ever._4ever_be_gw.business.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PayrollListEmployeeDto {
    private String employeeId;
    private String employeeName;
    private String departmentId;
    private String department;
    private String positionId;
    private String position;
}

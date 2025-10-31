package org.ever._4ever_be_gw.business.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDetailDto {
    @JsonProperty("employeeId")
    private String employeeId;

    @JsonProperty("employeeNumber")
    private String employeeNumber;

    @JsonProperty("name")
    private String name;

    @JsonProperty("email")
    private String email;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("position")
    private String position;

    @JsonProperty("department")
    private String department;

    @JsonProperty("statusCode")
    private String statusCode;

    @JsonProperty("hireDate")
    private String hireDate;

    @JsonProperty("birthDate")
    private String birthDate;

    @JsonProperty("address")
    private String address;

    @JsonProperty("createdAt")
    private String createdAt;

    @JsonProperty("updatedAt")
    private String updatedAt;

    @JsonProperty("trainings")
    private List<EmployeeTrainingItemDto> trainings;
}

package org.ever._4ever_be_gw.business.dto.employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
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

    @Min(value = 1, message = "부서 ID는 1 이상이어야 합니다")
    private Long departmentId;

    @Size(max = 20, message = "직급은 최대 20자까지 입력 가능합니다")
    private String positionId;

    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호는 010-XXXX-XXXX 형식이어야 합니다")
    private String phoneNumber;

    @Pattern(regexp = "^(MALE|FEMALE)$", message = "성별은 MALE 또는 FEMALE이어야 합니다")
    private String gender;

    private LocalDate birthDate;

    @Email(message = "올바른 이메일 형식이 아닙니다")
    @Size(max = 100, message = "이메일은 최대 100자까지 입력 가능합니다")
    private String email;

    @Size(max = 200, message = "주소는 최대 200자까지 입력 가능합니다")
    private String address;

    @Size(max = 200, message = "학력사항은 최대 200자까지 입력 가능합니다")
    private String academicHistory;

    @Size(max = 500, message = "경력사항은 최대 500자까지 입력 가능합니다")
    private String careerHistory;

}

package org.ever._4ever_be_gw.business.dto.employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


// 내부 직원 등록 dto
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class EmployeeCreateRequestDto {

    @NotBlank(message = "이름은 필수입니다")
    @Size(max = 50, message = "이름은 최대 50자까지 입력 가능합니다")
    private String name;

    @NotNull(message = "부서 ID는 필수입니다")
    @Min(value = 1, message = "부서 ID는 1 이상이어야 합니다")
    private Long departmentId;

    @NotNull(message = "직급 ID는 필수입니다")
    @Min(value = 1, message = "직급 ID는 1 이상이어야 합니다")
    private Long positionId;

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    @Size(max = 100, message = "이메일은 최대 100자까지 입력 가능합니다")
    private String email;

    @NotBlank(message = "전화번호는 필수입니다")
    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호는 010-XXXX-XXXX 형식이어야 합니다")
    private String phoneNumber;

    @NotNull(message = "입사일은 필수입니다")
    private LocalDate hireDate;

    @NotNull(message = "생년월일은 필수입니다")
    private LocalDate birthDate;

    @NotBlank(message = "성별은 필수입니다")
    @Pattern(regexp = "^(MALE|FEMALE)$", message = "성별은 MALE 또는 FEMALE이어야 합니다")
    private String gender;

    @Size(max = 200, message = "주소는 최대 200자까지 입력 가능합니다")
    private String address;

    @Size(max = 200, message = "학력은 최대 200자까지 입력 가능합니다")
    private String academicHistory;

    @Size(max = 500, message = "경력사항은 최대 500자까지 입력 가능합니다")
    private String careerHistory;

}

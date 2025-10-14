package org.ever._4ever_be_gw.business.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class LeaveRequestDto {

    @NotNull(message = "직원 ID는 필수입니다")
    private Long employeeId;

    @Pattern(regexp = "^(ANNUAL|SICK)$", message = "휴가 유형은 ANNUAL 또는 SICK이어야 합니다")
    private String leaveType;

    @NotNull(message = "시작일은 필수입니다")
    private LocalDate startDate;

    @NotNull(message = "종료일은 필수입니다")
    private LocalDate endDate;

}
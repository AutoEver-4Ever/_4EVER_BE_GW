package org.ever._4ever_be_gw.mockdata.business.dto;

import jakarta.validation.constraints.NotNull;
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
public class TrainingRequestDto {

    @NotNull(message = "프로그램 ID는 필수입니다")
    private Long programId;

}

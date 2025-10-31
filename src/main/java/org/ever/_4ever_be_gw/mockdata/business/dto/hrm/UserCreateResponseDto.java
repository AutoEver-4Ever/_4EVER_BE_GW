package org.ever._4ever_be_gw.mockdata.business.dto.hrm;

// HRM 내부 사용자 등록 성공 시 HRM 서버가 내려주는 응답을 매핑하기 위한 DTO

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateResponseDto {
    private UUID userId;          // 사용자 ID(UUID)

    private String userNumber;      // 직원 번호

    private String employeeId;      // 직원 식별자

    private LocalDateTime createdAt;  // 등록 시각

    private String status;          // 사용자의 상태(ACTIVE)
}

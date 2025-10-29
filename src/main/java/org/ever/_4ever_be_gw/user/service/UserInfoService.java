package org.ever._4ever_be_gw.user.service;

import org.ever._4ever_be_gw.common.exception.BusinessException;
import org.ever._4ever_be_gw.common.exception.ErrorCode;
import org.ever._4ever_be_gw.config.security.principal.EverUserPrincipal;
import org.ever._4ever_be_gw.user.dto.UserInfoResponse;
import org.springframework.stereotype.Service;

@Service
public class UserInfoService {

    public UserInfoResponse getUserInfo(EverUserPrincipal principal) {
        if (principal == null) {
            throw new BusinessException(ErrorCode.AUTH_TOKEN_REQUIRED);
        }

        return new UserInfoResponse(
            principal.getUserId(),
            principal.getLoginEmail(),
            principal.getUserRole(),
            principal.getUserType(),
            principal.getIssuedAt(),
            principal.getExpiresAt()
        );
    }
}

package org.ever._4ever_be_gw.user.controller;

import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_gw.common.response.ApiResponse;
import org.ever._4ever_be_gw.config.security.principal.EverJwtAuthenticationToken;
import org.ever._4ever_be_gw.config.security.principal.EverUserPrincipal;
import org.ever._4ever_be_gw.user.dto.UserInfoResponse;
import org.ever._4ever_be_gw.user.service.UserInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserInfoController {

    private final UserInfoService userInfoService;

    @GetMapping("/info")
    public ApiResponse<UserInfoResponse> getUserInfo(
        @AuthenticationPrincipal EverUserPrincipal principal,
        EverJwtAuthenticationToken authentication
    ) {
        UserInfoResponse data = userInfoService.getUserInfo(
            principal,
            authentication != null && authentication.getToken() != null
                ? authentication.getToken().getTokenValue()
                : null
        );
        return ApiResponse.success(data, "사용자 기본 정보를 조회했습니다.", HttpStatus.OK);
    }
}

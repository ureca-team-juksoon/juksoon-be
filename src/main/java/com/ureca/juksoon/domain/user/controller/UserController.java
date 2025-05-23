package com.ureca.juksoon.domain.user.controller;

import com.ureca.juksoon.domain.user.dto.request.UserRoleReq;
import com.ureca.juksoon.domain.user.dto.response.UserLoginRes;
import com.ureca.juksoon.domain.user.dto.response.UserRoleRes;
import com.ureca.juksoon.domain.user.service.UserService;
import com.ureca.juksoon.global.response.CommonResponse;
import com.ureca.juksoon.global.security.jwt.userdetail.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * 유저 권한 업데이트
     *
     * @param userDetails jwt 토큰 유저 정보
     * @param userRoleReq 권한 선택 폼으로 받은 유저가 선택한 권한
     */
    @Operation(summary = "유저 권한 변경", description = "DB의 사용자 역할 업데이트 + JWT 토큰 권한 갱신")
    @PostMapping("/user/role")
    public CommonResponse<?> updateRole(
            @Parameter   //(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,

            @Parameter(name = "userRoleReq", description = "유저 권한 폼 선택 권한", required = true)
            @RequestBody UserRoleReq userRoleReq,
            HttpServletResponse response) {
        UserRoleRes userRoleRes = userService.saveUserRole(userDetails.getUserId(), userRoleReq.getUserRole(), response);
        return CommonResponse.success(userRoleRes);
    }

    //테스트 매핑 401 return 해야함
    @GetMapping("/test")
    public CommonResponse<?> test() {
        return CommonResponse.success("권한없음");
    }

    @GetMapping("/login")
    public CommonResponse<UserLoginRes> login(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return userService.login(customUserDetails.getUserId());
    }
}

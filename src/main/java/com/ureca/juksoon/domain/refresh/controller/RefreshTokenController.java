package com.ureca.juksoon.domain.refresh.controller;

import com.ureca.juksoon.domain.refresh.service.RefreshTokenService;
import com.ureca.juksoon.global.response.CommonResponse;
import com.ureca.juksoon.global.response.CustomHeaderType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RefreshTokenController {
    private final RefreshTokenService refreshTokenService;

    @GetMapping("/refresh")
    public CommonResponse<?> getRefreshToken(HttpServletRequest request, HttpServletResponse response) {
        refreshTokenService.updateJwtToken(request.getHeader(CustomHeaderType.REFRESH_TOKEN.getHeader()), response);
        return CommonResponse.success(null);
    }
}

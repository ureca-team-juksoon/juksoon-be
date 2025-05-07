package com.ureca.juksoon.global.security.oauth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ureca.juksoon.global.refresh.service.RefreshTokenService;
import com.ureca.juksoon.domain.user.entity.UserRole;
import com.ureca.juksoon.global.response.CookieUtils;
import com.ureca.juksoon.global.response.CustomCookieType;
import com.ureca.juksoon.global.security.jwt.provider.JwtProvider;
import com.ureca.juksoon.global.security.jwt.provider.RefreshTokenProvider;
import com.ureca.juksoon.global.security.oauth.userdetail.PrincipalKey;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

/**
 * Authentication에서 여러가지 정보를 가져와 JWT토큰을 생성해준다.
 * 또한 RefreshToken도 생성해준다.
 * 그 후, response Header에 담아 클라이언트로 보내준다.
 * 클라이언트는 이를 LocalStoreage에 담아 줄 것이다.
 * 매 요청마다, JWT 토큰만, 헤더에 담아 요청을 보내주어야 한다.
 */

@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
private final String LOGIN_SUCCESS_REDIRECT_URI = "http://localhost:5173";

    private final JwtProvider jwtProvider;
    private final RefreshTokenProvider refreshTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User principal = (OAuth2User) authentication.getPrincipal();
        Long userId = (Long) principal.getAttributes().get(PrincipalKey.USER_ID.getKey());                              //Object로 반환해줘서 형변환
        UserRole role = UserRole.valueOf(principal.getAttributes().get(PrincipalKey.USER_ROLE.getKey()).toString());    //Object로 반환해주기 때문에, String으로 바꿔, UserRole.valueOf를 해준다.
        String jwt = generateJwtToken(userId, role);                  //jwt 생성
        String refreshToken = generateRefreshToken(userId, role);     //refresh token 생성

        log.info("JWT {}", jwt);
        log.info("Refresh-Token {}", refreshToken);

        refreshTokenService.save(refreshToken);                         //refresh token 저장한다. --> 서비스단에서 뭐, refreshTokenProvider 호출
        setBaseResponse(jwt, refreshToken, response);
    }

    private String generateJwtToken(Long userId, UserRole userRole) {
        return jwtProvider.generateJwtToken(userId, userRole);
    }

    private String generateRefreshToken(Long userId, UserRole userRole) {
        return refreshTokenProvider.generateRefreshToken(userId, userRole);
    }

    private void setCookieJwtAndRefreshToken(String jwt, String refreshToken, HttpServletResponse response) {
        setCookieJwt(jwt, response);
        setCookieRefreshToken(refreshToken, response);
    }

    private void setCookieJwt(String jwt, HttpServletResponse response) {
        CookieUtils.setResponseBasicCookie(CustomCookieType.AUTHORIZATION.getValue(), jwt, 50010000, response);
    }

    private void setCookieRefreshToken(String refreshToken, HttpServletResponse response){
        CookieUtils.setResponseBasicCookie(CustomCookieType.REFRESH_TOKEN.getValue(), refreshToken, 604800000, response);
    }

    private void setBaseResponse(String jwt, String refreshToken, HttpServletResponse response) throws IOException {
        setCookieJwtAndRefreshToken(jwt, refreshToken, response);     //쿠키에 토큰 넣기
        setRedirect(response);
        setFirstLoginAuthorization(response, jwt);
    }

    private void setRedirect(HttpServletResponse response) throws IOException {
        response.sendRedirect(LOGIN_SUCCESS_REDIRECT_URI);
    }

    private void setFirstLoginAuthorization(HttpServletResponse response, String jwt) throws IOException {
        UserRole role = jwtProvider.getRole(jwt);
        if (role == UserRole.ROLE_FIRST_LOGIN) {
            objectMapper.writeValue(response.getWriter(), String.format("{\"role\":\"%s\"}", role));
        }
    }
}


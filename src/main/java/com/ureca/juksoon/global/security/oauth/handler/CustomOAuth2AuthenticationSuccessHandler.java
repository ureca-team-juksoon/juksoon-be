package com.ureca.juksoon.global.security.oauth.handler;

import com.ureca.juksoon.domain.refresh.service.RefreshTokenService;
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
//    public static final String BEARER = "Bearer ";

    private final JwtProvider jwtProvider;
    private final RefreshTokenProvider refreshTokenProvider;
    private final RefreshTokenService refreshTokenService;

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

    private void setBaseResponse(String jwt, String refreshToken, HttpServletResponse response) throws IOException {
        setCookieJwtAndRefreshToken(jwt, refreshToken, response);     //쿠키에 토큰 넣기

        response.sendRedirect("http://localhost:5173");
        // ANONYMOUS 사용자인 경우 body에 role을 넣어줌
        UserRole role = jwtProvider.getRole(jwt);
        if (role == UserRole.ROLE_FIRST_LOGIN) {
            String body = String.format("{\"role\":\"%s\"}", role);
            response.getWriter().write(body);
        }
    }

    private String generateJwtToken(Long userId, UserRole userRole) {
        return jwtProvider.generateJwtToken(userId, userRole);
    }

    private String generateRefreshToken(Long userId, UserRole userRole) {
        return refreshTokenProvider.generateRefreshToken(userId, userRole);
    }

    /*
    response에 jwt토큰과 refresh 토큰을 넣어주는 메서드
    만약, userRole이 ROLE_ANONYMOUS이면, BODY에 ROLE_ANONYMOUS를 넣어준다.
    클라이언트는 이를 보고, 요청 URL을 home으로 보내던가, 권한 선택 후 권한 업데이트 API로 보낸다.
     */

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
}


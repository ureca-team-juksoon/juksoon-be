package com.ureca.juksoon.global.security.jwt.filter;

import com.ureca.juksoon.global.response.CookieUtils;
import com.ureca.juksoon.global.response.CustomCookieType;
import com.ureca.juksoon.global.security.jwt.provider.JwtProvider;
import com.ureca.juksoon.global.security.jwt.userdetail.CustomUserDetails;
import com.ureca.juksoon.global.security.oauth.userdetail.PrincipalKey;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 설명 : SecurityConfig에서 인증이 필요한 URI에 대해 모든 요청에 인증을 진행한다.
 * 요청 헤드에서 Authorization : Bearer {String 타입 JWT 토큰}에서 {String 타입 JWT 토큰}만 가져온다.
 * 가져온 JWT 토큰의 유효성 검사를 한다.
 * => JWT 토큰이 아예 없거나 이상한 경우: OAUTH2 인증 흐름으로 진행한다.(로그인 페이지 리다이렉트) == doFilter()
 * => JWT 토큰이 있지만 만료된 경우 : refresh 토큰을 통한 JWT 토큰 갱신 흐름 == 401 예외 response에 넣기
 * => JWT 토큰이 정상적으로 있는 경우 : JWT의 claims를 Authentication토큰에 넣어주고, 인가 흐름 == SecurityContextHolder 채우고 doFilter
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String OAUTH_LOGIN_REQUEST_URI_START_WITH = "/oauth2/authorization";
    private static final String OAUTH_SUCCESS_LOGIN_CODE_START_WITH = "/login/oauth2/code";
    private final JwtProvider jwtProvider;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) { //로그인 경로일 시 그냥 OAuth2로그인 흐름으로 진행
        String uri = request.getRequestURI();
        return isRelatedInOAuth(uri);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            validateCookieToken(request);               //jwt 검사
            String jwt = resolveToken(request);         //jwt 파싱 in cookie
            setSecurityContextHolder(jwt);              //인증 성공(컨텍스트 홀더 Authentication setting)
            filterChain.doFilter(request, response);    //다음으로
    }

    //jwt 토큰 검사 이 예외들은 CustomAuthenticationEntryPoint 에서 잡아줄거임.

    private void validateCookieToken(HttpServletRequest request){
        Cookie jwtCookie = CookieUtils.getCookie(CustomCookieType.AUTHORIZATION.getValue(), request);
        validateHasJwtInCookie(jwtCookie);
        validateJwtEmptyOrInvalid(jwtCookie);
        validateExpiredJwt(jwtCookie);
    }
    //토큰을 가져오기.

    private String resolveToken(HttpServletRequest request) {
        Cookie jwtCookie = CookieUtils.getCookie(CustomCookieType.AUTHORIZATION.getValue(), request);
        return jwtCookie.getValue();
    }
    //SecurityContextHolder에 Authentication 토큰을 세팅 해줌으로 인증 성공

    private void setSecurityContextHolder(String jwt) {
        CustomUserDetails customUserDetails = getCustomUserDetails(jwt);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    //UsernamePasswordAuthenticationToken을 생성하기 위한 정보들 Jwt에서 파싱
    private CustomUserDetails getCustomUserDetails(String jwt) {
        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put(PrincipalKey.USER_ID.getKey(), jwtProvider.getUserId(jwt));
        userDetails.put(PrincipalKey.USER_ROLE.getKey(), jwtProvider.getRole(jwt));
        return new CustomUserDetails(userDetails);
    }
    private void validateHasJwtInCookie(Cookie jwtCookie) {
        if(jwtCookie == null)
            throw new AuthenticationServiceException("토큰이 쿠키에 없습니다. 토큰을 Authorization 쿠키에 넣어 보내주세요.");
    }

    private void validateJwtEmptyOrInvalid(Cookie jwtCookie) {
        if(!StringUtils.hasText(jwtCookie.getValue()))
            throw new AuthenticationServiceException("토큰이 공백이거나, 형식이 이상합니다.");
    }

    private void validateExpiredJwt(Cookie jwtCookie) {
        if(jwtProvider.getTimeUntilExpiration(jwtCookie.getValue()) <= 0)
            throw new AuthenticationServiceException("토큰이 만료되었습니다. /refresh로 리다이렉트 해주세요.");
    }

    private boolean isRelatedInOAuth(String uri) {
        return uri.startsWith(OAUTH_LOGIN_REQUEST_URI_START_WITH) || uri.startsWith(OAUTH_SUCCESS_LOGIN_CODE_START_WITH);
    }
}
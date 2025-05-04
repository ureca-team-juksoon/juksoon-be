package com.ureca.juksoon.global.security.jwt.filter;

import com.ureca.juksoon.global.response.CustomHeaderType;
import com.ureca.juksoon.global.security.jwt.provider.JwtProvider;
import com.ureca.juksoon.global.security.jwt.userdetail.CustomUserDetails;
import com.ureca.juksoon.global.security.oauth.userdetail.PrincipalKey;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

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
    public static final String BEARER = "Bearer ";
    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = resolveToken(request);
        if(jwt == null){    //토큰이 없는 경우 그냥 OAuth2 로그인 흐름으로 진행
            filterChain.doFilter(request, response);
            return;
        }

        try{
            setSecurityContextHolder(jwt);
            filterChain.doFilter(request, response);
        }catch (ExpiredJwtException e){ //JWT 토큰이 있지만 만료된 경우(refresh 흐름=>403을 클라이언트로)
            setExceptionResponse(response, HttpStatus.FORBIDDEN, "토큰이 만료되었습니다.");
        }catch (JwtException | IllegalArgumentException e) { //JWT 토큰이 없거나 이상한 경우 401을 클라이언트로
            setExceptionResponse(response, HttpStatus.UNAUTHORIZED, "토큰이 없습니다.");
        }catch (Exception e) {
            log.error("알 수 없는 에러 발생 {}" + e.getMessage());
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
        }
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

    //토큰은 헤더에 Authorization: Bearer ey어쩌구~~로 온다. Bearer를 빼주고, ey어쩌구~~만 가져온다. ey어쩌구~~는 인코딩된 Jwt토큰임
    private String resolveToken(HttpServletRequest request) {
        String jwt = request.getHeader(CustomHeaderType.AUTHORIZATION.getHeader());
        if (StringUtils.hasText(jwt) && jwt.startsWith(BEARER)) {
            return jwt.substring(BEARER.length()).trim();
        }
        return null;
    }

    //예외 응답으로 처리
    private void setExceptionResponse(HttpServletResponse response, HttpStatus forbidden, String s)
            throws IOException {
        response.setStatus(forbidden.value());
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(s);
    }
}
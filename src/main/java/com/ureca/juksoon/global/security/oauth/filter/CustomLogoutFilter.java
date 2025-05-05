package com.ureca.juksoon.global.security.oauth.filter;

import com.ureca.juksoon.domain.refresh.service.RefreshTokenService;
import com.ureca.juksoon.global.response.CustomHeaderType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

/**
 * 로그아웃 필터
 * LogoutFilter.class 가 동작하기 전에 동작하는 CustomLogoutFilter
 * 1. POST, /logout 요청으로 들어왔는지 uri 검사
 * 2. refresh 토큰이 이미 null 인 경우 비정상적인 접근이라고 판단하여 BAD_REQUEST
 * 3. refresh 토큰이 DB 에서 검색이 안될 경우 동일하게 BAD_REQUEST
 * 4. DB에서 토큰 삭제 및 홀더 클리어
 * 5. 카카오 계정과 함께 로그아웃 또는 서비스만 로그아웃 하는 리다이렉트 주소 지정
 */
@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final RefreshTokenService refreshTokenService;
    @Value("${kakao.logout-uri}")
    private String logoutUri;

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.logout-redirect-uri}")
    private String logoutRedirectUri;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        String requestUri = request.getRequestURI();
        if (!requestUri.matches("^\\/logout$")) {

            filterChain.doFilter(request, response);
            return;
        }
        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {

            filterChain.doFilter(request, response);
            return;
        }

        String refresh = request.getHeader(CustomHeaderType.REFRESH_TOKEN.getHeader());

        if (refresh == null) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Boolean isExist = refreshTokenService.existsByToken(refresh);
        if (!isExist) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        refreshTokenService.deleteByToken(refresh);

        response.setStatus(HttpServletResponse.SC_OK);
        SecurityContextHolder.clearContext();

        String url = logoutUri + "?client_id=" + clientId + "&logout_redirect_uri=" + logoutRedirectUri;

        response.sendRedirect(url);
    }
}

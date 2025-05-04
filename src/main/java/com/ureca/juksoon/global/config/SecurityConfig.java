package com.ureca.juksoon.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ureca.juksoon.domain.refresh.service.RefreshTokenService;
import com.ureca.juksoon.domain.user.entity.UserRole;
import com.ureca.juksoon.global.security.jwt.filter.JwtAuthenticationFilter;
import com.ureca.juksoon.global.security.jwt.provider.JwtProvider;
import com.ureca.juksoon.global.security.jwt.provider.RefreshTokenProvider;
import com.ureca.juksoon.global.security.oauth.handler.CustomOAuth2AuthenticationSuccessHandler;
import com.ureca.juksoon.global.security.oauth.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;
    private final RefreshTokenProvider refreshTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final JwtProvider jwtProvider;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http    //rest api 기본 설정 추가
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(AbstractHttpConfigurer::disable);

        http    //커스텀 필터 추가
                .addFilterBefore(jwtAuthenticationFilter(), OAuth2AuthorizationRequestRedirectFilter.class);

        http    //OAuth2.0 로그인 흐름 설정
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(info -> info.userService(customOAuth2UserService))
                        .successHandler(new CustomOAuth2AuthenticationSuccessHandler(jwtProvider, refreshTokenProvider, refreshTokenService)));

        http    //인가 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login/**").permitAll()
                        .requestMatchers( "/oauth2/**").permitAll()
                        .requestMatchers("/refresh").permitAll()
                        .requestMatchers("/user/role").hasAnyAuthority(UserRole.ROLE_FIRST_LOGIN.getUserRole())
                        .anyRequest().authenticated());

        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception{
        return new JwtAuthenticationFilter(jwtProvider);
    }


    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }
}

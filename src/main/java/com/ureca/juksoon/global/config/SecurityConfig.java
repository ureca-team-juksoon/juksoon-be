package com.ureca.juksoon.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ureca.juksoon.global.refresh.service.RefreshTokenService;
import com.ureca.juksoon.domain.user.entity.UserRole;
import com.ureca.juksoon.global.security.exception.CustomAccessDeniedHandler;
import com.ureca.juksoon.global.security.exception.CustomAuthenticationEntryPoint;
import com.ureca.juksoon.global.security.exception.SecurityExceptionResponseSetter;
import com.ureca.juksoon.global.security.jwt.filter.JwtAuthenticationFilter;
import com.ureca.juksoon.global.security.jwt.provider.JwtProvider;
import com.ureca.juksoon.global.security.jwt.provider.RefreshTokenProvider;
import com.ureca.juksoon.global.security.oauth.filter.CustomLogoutFilter;
import com.ureca.juksoon.global.security.oauth.handler.CustomOAuth2AuthenticationSuccessHandler;
import com.ureca.juksoon.global.security.oauth.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    @Value("${cors.allowed-origin}")
    private String cors_origin;
    private final SecurityExceptionResponseSetter exceptionResponseSetter;
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
                .sessionManagement(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        http    //OAuth2.0 로그인 흐름 설정
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(info -> info.userService(customOAuth2UserService))
                        .successHandler(customOAuth2SuccessHandler()));

        http    //커스텀 필터들 추가
                .addFilterBefore(jwtAuthenticationFilter(), OAuth2AuthorizationRequestRedirectFilter.class)
                .addFilterBefore(customLogoutFilter(), LogoutFilter.class);

        http    //인가 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login/**").permitAll()
                        .requestMatchers( "/auth/**").permitAll()
                        .requestMatchers("/refresh").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/user/role").hasAnyAuthority(UserRole.ROLE_FIRST_LOGIN.getUserRole())
                        .anyRequest().authenticated());

        http    //예외 발생시 예외 처리 핸들러들
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthenticationEntryPoint())
                        .accessDeniedHandler(customAccessDeniedHandler()));

        return http.build();
    }

    @Bean
    public CustomLogoutFilter customLogoutFilter(){
        return new CustomLogoutFilter(refreshTokenService);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception{
        return new JwtAuthenticationFilter(jwtProvider);
    }


    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }

    @Bean
    public AuthenticationEntryPoint customAuthenticationEntryPoint(){
        return new CustomAuthenticationEntryPoint(exceptionResponseSetter);
    }

    @Bean
    public AccessDeniedHandler customAccessDeniedHandler(){
        return new CustomAccessDeniedHandler(exceptionResponseSetter);
    }

    @Bean
    public CustomOAuth2AuthenticationSuccessHandler customOAuth2SuccessHandler() {
        return new CustomOAuth2AuthenticationSuccessHandler(
                jwtProvider,
                refreshTokenProvider,
                refreshTokenService,
                objectMapper()
                );
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration(); // cors 설정
        configuration.addAllowedOrigin(cors_origin); // 허용할 origin 설정
        configuration.addAllowedOrigin("추가 origin 주소");
        configuration.addAllowedMethod("*"); // 모든 HTTP 메소드 허용
        configuration.addAllowedHeader("*"); // 모든 헤더 허용
        configuration.setAllowCredentials(true); // 쿠키 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 위 설정 적용
        return source;
    }
}

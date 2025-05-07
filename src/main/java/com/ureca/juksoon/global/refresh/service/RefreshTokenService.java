package com.ureca.juksoon.global.refresh.service;

import com.ureca.juksoon.global.refresh.entity.RefreshToken;
import com.ureca.juksoon.global.refresh.repository.RefreshTokenRepository;
import com.ureca.juksoon.domain.user.entity.User;
import com.ureca.juksoon.domain.user.entity.UserRole;
import com.ureca.juksoon.domain.user.repository.UserRepository;
import com.ureca.juksoon.global.response.CookieUtils;
import com.ureca.juksoon.global.response.CustomCookieType;
import com.ureca.juksoon.global.security.jwt.provider.JwtProvider;
import com.ureca.juksoon.global.security.jwt.provider.RefreshTokenProvider;
import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenProvider refreshTokenProvider;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public void save(String rawRefreshToken) {
        refreshTokenRepository.save(getRefreshToken(rawRefreshToken));
    }

    @Transactional(readOnly = true)
    public void updateJwtToken(String rawRefreshToken, HttpServletResponse response) {
        RefreshToken dbRefreshToken = refreshTokenRepository.findByToken(rawRefreshToken)
                .orElseThrow(() -> new NoSuchElementException("Refresh token not found"));
        LocalDateTime now = LocalDateTime.now();

        if (isExpired(dbRefreshToken, now)) throw new IllegalArgumentException("Refresh token expired");

        User user = dbRefreshToken.getUser();
        UserRole role = user.getRole();
        String newAccessToken = jwtProvider.generateJwtToken(user.getId(), role);
        CookieUtils.setResponseBasicCookie(CustomCookieType.AUTHORIZATION.getValue(), newAccessToken, 50010000, response);
    }

    private boolean isExpired(RefreshToken dbRefreshToken, LocalDateTime now) {
        return dbRefreshToken.getExpiredAt().isBefore(now);
    }

    private RefreshToken getRefreshToken(String rawRefreshToken) {
        Optional<User> user = userRepository.findById(refreshTokenProvider.getUserId(rawRefreshToken));

        return RefreshToken.builder()
                .token(rawRefreshToken)
                .expiredAt(refreshTokenProvider.getExpiredAt(rawRefreshToken))
                .user(user.get())
                .build();
    }

    public Boolean existsByToken(String refresh) {
        return refreshTokenRepository.existsByToken(refresh);
    }

    @Transactional
    public void deleteByToken(String refresh) {
        refreshTokenRepository.deleteByToken(refresh);
    }
}

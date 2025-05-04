package com.ureca.juksoon.domain.user.service;

import com.ureca.juksoon.domain.user.dto.UserRoleReq;
import com.ureca.juksoon.domain.user.dto.UserRoleRes;
import com.ureca.juksoon.domain.user.entity.User;
import com.ureca.juksoon.domain.user.entity.UserRole;
import com.ureca.juksoon.domain.user.repository.UserRepository;
import com.ureca.juksoon.global.security.jwt.provider.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Transactional
    public UserRoleRes saveUserRole(Long userId, UserRole userRole, HttpServletResponse response) {
        User savedUser = updateUserRole(userId, userRole);
        sendNewJwt(response, savedUser);
        return new UserRoleRes(savedUser.getRole());
    }

    private User updateUserRole(Long userId, UserRole userRole) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found : " + userId));
        user.setRole(userRole);
        return userRepository.save(user);
    }

    private void sendNewJwt(HttpServletResponse response, User savedUser) {
        String newToken = jwtProvider.generateJwtToken(savedUser.getId(), savedUser.getRole());
        response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + newToken);
    }
}

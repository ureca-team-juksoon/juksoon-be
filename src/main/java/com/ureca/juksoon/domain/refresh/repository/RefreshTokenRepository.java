package com.ureca.juksoon.domain.refresh.repository;

import com.ureca.juksoon.domain.refresh.entity.RefreshToken;
import com.ureca.juksoon.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUser(User user);

    Boolean existsByToken(String token);

    void deleteByToken(String token);
}

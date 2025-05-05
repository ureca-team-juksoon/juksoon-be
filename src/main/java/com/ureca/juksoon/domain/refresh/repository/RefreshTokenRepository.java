package com.ureca.juksoon.domain.refresh.repository;

import com.ureca.juksoon.domain.refresh.entity.RefreshToken;
import java.util.Optional;

import com.ureca.juksoon.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUser(User user);

    Boolean existsByToken(String token);

    @Transactional
    void deleteByToken(String token);
}

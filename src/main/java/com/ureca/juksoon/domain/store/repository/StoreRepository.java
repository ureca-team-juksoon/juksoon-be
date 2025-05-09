package com.ureca.juksoon.domain.store.repository;

import com.ureca.juksoon.domain.store.entity.Store;
import com.ureca.juksoon.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {
    Boolean existsByUser(User user);

    Optional<Store> findByUser(User user);

    Store findByUserId(Long userId);
}

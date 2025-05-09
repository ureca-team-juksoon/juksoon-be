package com.ureca.juksoon.domain.store.repository;

import com.ureca.juksoon.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {
    Boolean existsByUserId(Long userId);

    Optional<Store> findByUserId(Long userId);
}

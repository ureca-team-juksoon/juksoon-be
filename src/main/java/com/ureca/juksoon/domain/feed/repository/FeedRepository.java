package com.ureca.juksoon.domain.feed.repository;

import com.ureca.juksoon.domain.feed.entity.Feed;
import jakarta.persistence.LockModeType;
import com.ureca.juksoon.domain.feed.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

import java.util.List;

public interface FeedRepository extends JpaRepository<Feed, Long>, CustomFeedRepository {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select f from Feed f where f.id = :feedId")
    Optional<Feed> findByIdForUpdate(@Param("feedId") Long feedId);
    List<Feed> findAllByStatusAndExpiredAt(Status status, String expiredAt);
}

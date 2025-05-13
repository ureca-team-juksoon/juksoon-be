package com.ureca.juksoon.domain.feed.repository;

import com.ureca.juksoon.domain.feed.entity.Feed;
import com.ureca.juksoon.domain.feed.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedRepository extends JpaRepository<Feed, Long>, CustomFeedRepository {
    List<Feed> findAllByStatusAndExpiredAt(Status status, String expiredAt);
}

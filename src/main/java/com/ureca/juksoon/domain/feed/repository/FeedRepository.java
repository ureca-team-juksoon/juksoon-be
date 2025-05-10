package com.ureca.juksoon.domain.feed.repository;

import com.ureca.juksoon.domain.feed.entity.Feed;
import com.ureca.juksoon.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedRepository extends JpaRepository<Feed, Long> {
    List<Feed> findAllByStore(Store store);
}

package com.ureca.juksoon.domain.feed.repository;

import com.ureca.juksoon.domain.feed.entity.Feed;
import com.ureca.juksoon.domain.feed.entity.FeedFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedFileRepository extends JpaRepository<FeedFile, Long>, FeedFileJdbcRepository {
    List<FeedFile> findAllByFeed(Feed feed);
}

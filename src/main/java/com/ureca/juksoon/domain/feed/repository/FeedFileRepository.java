package com.ureca.juksoon.domain.feed.repository;

import com.ureca.juksoon.domain.feed.entity.FeedFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedFileRepository extends JpaRepository<FeedFile, Long> {
}

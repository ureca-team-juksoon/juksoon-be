package com.ureca.juksoon.domain.feed.repository;

import com.ureca.juksoon.domain.feed.entity.FeedFile;

import java.util.List;

public interface FeedFileJdbcRepository {
    void saveAllFeedFiles(List<FeedFile> files);
}

package com.ureca.juksoon.domain.feed.repository;

import com.ureca.juksoon.domain.feed.entity.Category;
import com.ureca.juksoon.domain.feed.entity.Feed;
import com.ureca.juksoon.domain.feed.entity.SortType;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomFeedRepository {
    List<Feed> findAllByFiltering(Pageable pageable, boolean isAvailable, SortType sortType, Category category, String keyword);
}

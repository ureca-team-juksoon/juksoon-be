package com.ureca.juksoon.domain.feed.repository;

import com.ureca.juksoon.domain.feed.entity.Category;
import com.ureca.juksoon.domain.feed.entity.Feed;
import com.ureca.juksoon.domain.feed.entity.SortType;
import com.ureca.juksoon.domain.store.entity.Store;
import com.ureca.juksoon.domain.user.entity.User;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomFeedRepository {
    List<Feed> findAllByFiltering(Pageable pageable, boolean isAvailable, SortType sortType, Category category, String keyword);
    List<Feed> findAllByUserOrderByFeedIdDesc(Pageable pageable, User user, Long lastFeedId);
    List<Feed> findAllByStoreOrderByFeedIdDesc(Pageable pageable, Store store, Long lastFeedId);
    void deactivateAllStatus();
    void activateAllStatus();
}

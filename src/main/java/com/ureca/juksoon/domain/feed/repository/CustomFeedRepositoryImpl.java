package com.ureca.juksoon.domain.feed.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ureca.juksoon.domain.feed.entity.*;
import com.ureca.juksoon.domain.store.entity.Store;
import com.ureca.juksoon.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.ureca.juksoon.domain.feed.entity.QFeed.feed;
import static com.ureca.juksoon.domain.reservation.entity.QReservation.reservation;
import static com.ureca.juksoon.domain.store.entity.QStore.store;

@Repository
@RequiredArgsConstructor
public class CustomFeedRepositoryImpl implements CustomFeedRepository {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 필터링 시 전체 개수 count
     */
    @Override
    public Long countAllByFiltering(boolean isAvailable, Category category, String keyword) {
        return jpaQueryFactory
            .select(feed.count())
            .from(feed)
            .leftJoin(feed.store, store)
            .where(
                isOpen(isAvailable),
                categoryEq(category),
                keywordContains(keyword)
            )
            .fetchOne();
    }

    /**
     * Feed 필터링 적용 전체 검색
     */
    @Override
    public List<Feed> findPageByFiltering(Pageable pageable, boolean isAvailable, SortType sortType, Category category, String keyword) {
        return jpaQueryFactory
            .selectFrom(feed)
            .leftJoin(feed.store, store)
            .where(
                isOpen(isAvailable),
                categoryEq(category),
                keywordContains(keyword)
            )
            .orderBy(getSortTypeSpecifier(sortType))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }

    @Override
    public List<Feed> findAllByUserOrderByFeedIdDesc(Pageable pageable, User user, Long lastFeedId) {
        return jpaQueryFactory
            .select(feed)
            .from(reservation)
            .join(reservation.feed, feed)
            .orderBy(feed.id.desc())
            .where(
                reservation.user.eq(user),
                ltFeedId(lastFeedId))
            .limit(pageable.getPageSize() + 1) // 다음 페이지 유무 판단을 위해
            .fetch();
    }

    @Override
    public List<Feed> findAllByStoreOrderByFeedIdDesc(Pageable pageable, Store store, Long lastFeedId) {
        return jpaQueryFactory
            .selectFrom(feed)
            .orderBy(feed.id.desc())
            .where(
                feed.store.eq(store),
                ltFeedId(lastFeedId))
            .limit(pageable.getPageSize() + 1) // 다음 페이지 유무 판단을 위해
            .fetch();
    }

    // 정렬 순서 설정
    private OrderSpecifier<?> getSortTypeSpecifier(SortType sortType) {
        return switch (sortType) {
            case PRICE_ASC -> new OrderSpecifier<>(Order.ASC, QFeed.feed.price);
            case RECENT -> new OrderSpecifier<>(Order.DESC, QFeed.feed.id);
            default -> new OrderSpecifier<>(Order.DESC, QFeed.feed.registeredUser.doubleValue().divide(QFeed.feed.maxUser.doubleValue()));
        };
    }

    // 신청 가능 여부 필터링
    private BooleanExpression isOpen(boolean isAvailable) {
        return isAvailable ? feed.status.in(Status.OPEN, Status.UPCOMING) : null;
    }

    // 카테고리 필터링
    private BooleanExpression categoryEq(Category category) {
        return category != null ? feed.category.eq(category) : null;
    }

    // 키워드 필터링
    private BooleanExpression keywordContains(String keyword) {
        if (keyword == null || keyword.isBlank()) return null;

        return feed.title.contains(keyword)
            .or(feed.store.name.contains(keyword));
    }

    // 커서 설정 (id가 클수록 최신)
    private BooleanExpression ltFeedId(Long lastFeedId) {
        if(lastFeedId == null) return null;
        return feed.id.lt(lastFeedId);
    }
}

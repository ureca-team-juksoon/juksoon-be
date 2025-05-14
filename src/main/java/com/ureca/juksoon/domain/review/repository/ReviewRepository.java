package com.ureca.juksoon.domain.review.repository;

import com.ureca.juksoon.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // ROLE_OWNER
    @Query("SELECT r FROM Review r JOIN FETCH r.user WHERE r.feed.id = :feedId ")
    List<Review> findAllByFeedId(@Param("feedId") Long feedId);

    // ROLE_TESTER
    @Query("SELECT r FROM Review r JOIN FETCH r.user WHERE r.feed.id = :feedId AND r.user.id = :userId")
    Optional<Review> findByFeedIdAndUserId(@Param("feedId") Long feedId, @Param("userId") Long userId);
}

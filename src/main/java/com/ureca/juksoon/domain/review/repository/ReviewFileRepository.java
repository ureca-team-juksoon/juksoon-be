package com.ureca.juksoon.domain.review.repository;

import com.ureca.juksoon.domain.review.entity.ReviewFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewFileRepository extends JpaRepository<ReviewFile, Long> {
    List<ReviewFile> findAllByReview_IdIn(List<Long> reviewIds);

    List<ReviewFile> findAllByReviewId(Long reviewId);


}

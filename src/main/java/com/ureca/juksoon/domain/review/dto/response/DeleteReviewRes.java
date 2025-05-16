package com.ureca.juksoon.domain.review.dto.response;

import com.ureca.juksoon.domain.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DeleteReviewRes {
    private String title;
    private String contents;

    public DeleteReviewRes(Review review) {
        this.title = review.getTitle();
        this.contents = review.getContent();
    }
}

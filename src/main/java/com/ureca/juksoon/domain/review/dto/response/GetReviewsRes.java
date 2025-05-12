package com.ureca.juksoon.domain.review.dto.response;

import com.ureca.juksoon.domain.review.dto.ReviewWithFiles;
import com.ureca.juksoon.domain.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetReviewsRes {
    private List<ReviewWithFiles> reviews;
}

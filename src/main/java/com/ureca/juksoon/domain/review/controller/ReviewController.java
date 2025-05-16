package com.ureca.juksoon.domain.review.controller;

import com.ureca.juksoon.domain.review.dto.request.ReviewReq;
import com.ureca.juksoon.domain.review.dto.response.CreateReviewRes;
import com.ureca.juksoon.domain.review.dto.response.DeleteReviewRes;
import com.ureca.juksoon.domain.review.dto.response.GetReviewsRes;
import com.ureca.juksoon.domain.review.dto.response.ModifyReviewRes;
import com.ureca.juksoon.domain.review.service.ReviewService;
import com.ureca.juksoon.global.response.CommonResponse;
import com.ureca.juksoon.global.security.jwt.userdetail.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("feeds/review/{feed_id}")
public class ReviewController {

    private final ReviewService reviewService;

    // create
    @PostMapping
    public CommonResponse<CreateReviewRes> createReview(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @ModelAttribute ReviewReq request,
            @PathVariable("feed_id") Long feedId) {

        return CommonResponse.success(reviewService.createReview(customUserDetails.getUserId(), feedId, request));
    }

    // read
    @GetMapping
    public CommonResponse<GetReviewsRes> getReviews(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("feed_id") Long feedId) {

        return CommonResponse.success(reviewService.getReviews(customUserDetails.getUserId(), customUserDetails.getUserRole(), feedId));
    }

    // update
    @PatchMapping
    public CommonResponse<ModifyReviewRes> updteReview(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @ModelAttribute ReviewReq request,
            @PathVariable("feed_id") Long feedId) {

        return CommonResponse.success(reviewService.updateReview(customUserDetails.getUserId(), feedId, request));
    }

    // delete
    @DeleteMapping
    public CommonResponse<DeleteReviewRes> deleteReview(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("feed_id") Long feedId) {

        return CommonResponse.success(reviewService.deleteReview(customUserDetails.getUserId(), feedId));
    }
}

package com.ureca.juksoon.domain.review.controller;

import com.ureca.juksoon.domain.review.dto.request.ReviewReq;
import com.ureca.juksoon.domain.review.service.ReviewService;
import com.ureca.juksoon.global.response.CommonResponse;
import com.ureca.juksoon.global.security.jwt.userdetail.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("feeds/review/{feed_id}")
public class ReviewController {

    private final ReviewService reviewService;

    // create
    @PostMapping
    public CommonResponse<?> createReview(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @ModelAttribute ReviewReq request,
            @PathVariable("feed_id") Long feedId){
        return CommonResponse.success(reviewService.createReview(customUserDetails.getUserId(), feedId, request));
    }

    // read
    @GetMapping
    public CommonResponse<?> getReviews(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("feed_id") Long feedId){
        return CommonResponse.success(reviewService.getReviews(customUserDetails.getUserId(), customUserDetails.getUserRole(), feedId));
    }

    // update
    @PostMapping
    public CommonResponse<?> updateReview(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @ModelAttribute ReviewReq request,
            @PathVariable("feed_id") Long feedId){

        return CommonResponse.success(reviewService.updateReview(customUserDetails.getUserId(), feedId, request));
    }



    // delete

}

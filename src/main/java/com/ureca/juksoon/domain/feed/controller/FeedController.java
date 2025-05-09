package com.ureca.juksoon.domain.feed.controller;

import com.ureca.juksoon.domain.feed.dto.request.CreateFeedReq;
import com.ureca.juksoon.domain.feed.dto.responce.CreateFeedRes;
import com.ureca.juksoon.domain.feed.service.FeedService;
import com.ureca.juksoon.global.response.CommonResponse;
import com.ureca.juksoon.global.s3.S3Service;
import com.ureca.juksoon.global.security.jwt.userdetail.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;
    private final S3Service s3Service;

    /**
     * 피드 생성
     * @param userDetail 사용자 정보
     * @param req 피드 생성을 위한 정보
     */
    @Operation(summary = "피드 생성", description = "피드 생성: 로그인 필요")
    @PostMapping
    public CommonResponse<CreateFeedRes> createFeed(
        @Parameter(description = "사용자정보", required = true)
        @AuthenticationPrincipal CustomUserDetails userDetail,
        @Parameter(description = "피드 생성 정보", required = true)
        @Valid @ModelAttribute CreateFeedReq req) {
        CreateFeedRes feed = feedService.createFeed(userDetail.getUserId(), req);
        return CommonResponse.success(feed);
    }



}

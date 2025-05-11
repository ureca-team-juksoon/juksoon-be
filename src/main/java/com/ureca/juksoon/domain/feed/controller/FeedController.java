package com.ureca.juksoon.domain.feed.controller;

import com.ureca.juksoon.domain.feed.dto.request.CreateFeedReq;
import com.ureca.juksoon.domain.feed.dto.request.ModifyFeedReq;
import com.ureca.juksoon.domain.feed.dto.responce.*;
import com.ureca.juksoon.domain.feed.entity.Category;
import com.ureca.juksoon.domain.feed.entity.SortType;
import com.ureca.juksoon.domain.feed.service.FeedService;
import com.ureca.juksoon.global.response.CommonResponse;
import com.ureca.juksoon.global.security.jwt.userdetail.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    /**
     * 피드 상세 조회 (Home)
     * @param pageable
     * @param keyword
     * @param category
     * @param isAvailable
     * @param sortType
     */
    @Operation(summary = "피드 상세 조회(Home)", description = "피드 전체 조회(Home)")
    @GetMapping
    public CommonResponse<GetHomeInfoRes> getHomeInfo(
        @ParameterObject Pageable pageable,
        @Parameter(description = "검색어")
        @RequestParam(required = false) String keyword,
        @Parameter(description = "카테고리")
        @RequestParam(required = false) Category category,
        @Parameter(description = "신청 가능 여부")
        @RequestParam(required = false) boolean isAvailable,
        @Parameter(description = "피드 순서", required = true)
        @RequestParam SortType sortType) {
        return CommonResponse.success(feedService.getHomeInfo(pageable, keyword, category, isAvailable, sortType));
    }

    /**
     * 피드 상세 조회 (Mypage)
     * @param userDetail 사용자 정보
     */
    @Operation(summary = "피드 상세 조회(Mypage)", description = "피드 전체 조회(Mypage): 로그인 필요")
    @GetMapping("/mypage")
    public CommonResponse<GetMypageInfoRes> getMypageInfo(
        @Parameter(description = "사용자정보", required = true)
        @AuthenticationPrincipal CustomUserDetails userDetail,
        @ParameterObject Pageable pageable,
        @Parameter(description = "마지막 Feed Id")
        @RequestParam(required = false) Long lastFeedId) {
        return CommonResponse.success(feedService.getMypageInfo(userDetail.getUserId(), pageable, lastFeedId));
    }

    /**
     * 피드 단일 조회
     * @param feedId 피드 id
     */
    @Operation(summary = "피드 상세 조회(Mypage)", description = "피드 상세 조회")
    @GetMapping("/{feedId}")
    public CommonResponse<GetFeedDetailRes> getFeedDetail(
        @Parameter(description = "피드 id", required = true)
        @PathVariable Long feedId) {
        return CommonResponse.success(feedService.getFeedDetail(feedId));
    }

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
        return CommonResponse.success(feedService.createFeed(userDetail.getUserId(), req));
    }

    /**
     * 피드 삭제
     * @param userDetail 사용자 정보
     * @param feedId 삭제할 feed Id
     */
    @Operation(summary = "피드 삭제", description = "피드 삭제: 로그인 필요")
    @DeleteMapping("/{feedId}")
    public CommonResponse<DeleteFeedRes> deleteFeed(
        @Parameter(description = "사용자정보", required = true)
        @AuthenticationPrincipal CustomUserDetails userDetail,
        @Parameter(description = "피드 id", required = true)
        @PathVariable Long feedId) {
        return CommonResponse.success(feedService.deleteFeed(userDetail.getUserId(), feedId));
    }

    /**
     * 피드 수정
     * @param userDetail 사용자 정보
     * @param feedId 수정할 feed Id
     * @param req 피드 수정을 위한 정보
     */
    @Operation(summary = "피드 수정", description = "피드 수정: 로그인 필요")
    @PatchMapping("/{feedId}")
    public CommonResponse<ModifyFeedRes> modifyFeed(
        @Parameter(description = "사용자정보", required = true)
        @AuthenticationPrincipal CustomUserDetails userDetail,
        @Parameter(description = "피드 id", required = true)
        @PathVariable Long feedId,
        @Parameter(description = "피드 수정 정보", required = true)
        @Valid @ModelAttribute ModifyFeedReq req) {
        return CommonResponse.success(feedService.modifyFeed(userDetail.getUserId(), feedId, req));
    }
}

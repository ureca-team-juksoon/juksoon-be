package com.ureca.juksoon.domain.feed.controller;

import com.ureca.juksoon.global.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feed")
public class FeedController {

    /**
     * 게시글 수정 (swagger 예시용)
     * @param feedId 게시글 ID
     * @param req 게시글 수정을 위한 정보
     */
//    @Operation(summary = "게시글 생성", description = "게시글 생성: 로그인 필요")
//    @PostMapping
//    public CommonResponse<ModifyFeedRes> modifyFeed(
//        @Parameter(name = "feedId", description = "게시글 id", example = "1", required = true)
//        @PathVariable Long feedId,
//        @RequestBody String req) {
//        return CommonResponse.success(feedService.modifyFeed(feedId, req));
//    }
}

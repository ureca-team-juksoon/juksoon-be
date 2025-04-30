package com.ureca.juksoon.domain.post.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/post")
public class PostController {

    /**
     * 게시글 수정 (swagger 예시용)
     * @param postId 게시글 ID
     * @param req 게시글 수정을 위한 정보
     */
//    @Operation(summary = "게시글 생성", description = "게시글 생성: 로그인 필요")
//    @PostMapping
//    public CommonResponse<ModifyPostRes> createPost(
//        @Parameter(name = "postId", description = "게시글 id", example = "1", required = true)
//        @PathVariable Long postId,
//        @RequestBody String req) {
//        return CommonResponse.success(postService.modifyPost(postId, req));
//    }
}

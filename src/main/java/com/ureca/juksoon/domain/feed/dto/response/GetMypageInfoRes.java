package com.ureca.juksoon.domain.feed.dto.response;

import com.ureca.juksoon.domain.user.entity.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetMypageInfoRes {

    @Schema(description = "(가게/유저) id", example = "1")
    private Long id;

    @Schema(description = "(가게/유저) 이름", example = "빽다방")
    private String name;

    @Schema(description = "역할", example = "1")
    private UserRole role;

    @Schema(description = "피드 List", example = "피드 List")
    private List<GetFeedRes> feedList;
}

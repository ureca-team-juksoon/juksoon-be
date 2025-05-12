package com.ureca.juksoon.domain.feed.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetHomeInfoRes {
    @Schema(description = "피드 List", example = "피드 List")
    private List<GetFeedRes> feedList;
}

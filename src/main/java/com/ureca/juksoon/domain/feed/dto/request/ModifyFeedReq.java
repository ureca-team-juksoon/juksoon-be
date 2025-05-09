package com.ureca.juksoon.domain.feed.dto.request;

import com.ureca.juksoon.domain.feed.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class ModifyFeedReq {
    @Schema(description = "피드 제목", example = "[수정] 햄버거 드시고 후기 남겨주세요")
    private String title;

    @Schema(description = "내용", example = "수정 햄버거가 공짜!")
    private String content;

    @Schema(description = "방문 일자", example = "2025-06-20")
    private String expiredAt;

    @Schema(description = "카테고리", example = "한식")
    private Category category;

    @Schema(description = "가격", example = "1200")
    private int price;

    @Schema(description = "이미지 List", example = "링크, 링크")
    List<MultipartFile> images;

    @Schema(description = "영상", example = "링크")
    MultipartFile video;
}

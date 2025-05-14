package com.ureca.juksoon.domain.review.dto.response;

import com.ureca.juksoon.domain.common.FileType;
import com.ureca.juksoon.domain.review.entity.Review;
import com.ureca.juksoon.domain.review.entity.ReviewFile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class ReviewWithFiles {
    private Long id;
    private String writer;
    private String title;
    private String content;
    private List<String> imageUrls;
    private String videoUrl;

    public static ReviewWithFiles from(Review review, List<ReviewFile> files) {
        return ReviewWithFiles.builder()
                .id(review.getId())
                .writer(review.getUser().getNickname())
                .title(review.getTitle())
                .content(review.getContent())
                .imageUrls(files.stream()
                        .filter(f -> f.getType() == FileType.IMAGE)
                        .map(ReviewFile::getUrl)
                        .toList())
                .videoUrl(files.stream()
                        .filter(f -> f.getType() == FileType.VIDEO)
                        .map(ReviewFile::getUrl)
                        .findFirst()
                        .orElse(null))
                .build();
    }
}

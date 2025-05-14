package com.ureca.juksoon.domain.review.entity;

import com.ureca.juksoon.domain.common.BaseEntity;
import com.ureca.juksoon.domain.common.FileType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewFile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "type", nullable = false)
    private FileType type;

    @ManyToOne
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    public static ReviewFile of(Review review, String url, FileType fileType){
        return ReviewFile.builder()
                .url(url)
                .type(fileType)
                .review(review)
                .build();
    }
}

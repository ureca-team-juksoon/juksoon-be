package com.ureca.juksoon.domain.review.entity;

import com.ureca.juksoon.domain.common.BaseEntity;
import com.ureca.juksoon.domain.common.FileType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reviewfile")
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

    public static ReviewFile of(Review review, String url, FileType fileType) {
        return ReviewFile.builder()
                .url(url)
                .type(fileType)
                .review(review)
                .build();
    }
}

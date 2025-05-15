package com.ureca.juksoon.domain.review.entity;

import com.ureca.juksoon.domain.common.BaseEntity;
import com.ureca.juksoon.domain.feed.entity.Feed;
import com.ureca.juksoon.domain.review.dto.request.ReviewReq;
import com.ureca.juksoon.domain.user.entity.User;
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
@Builder
@Table(name = "review")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "feed_id", nullable = false)
    private Feed feed;

    public static Review of(User user, Feed feed, ReviewReq req) {
        return Review.builder()
                .title(req.getTitle())
                .content(req.getContent())
                .user(user)
                .feed(feed)
                .build();
    }

    public void update(ReviewReq req) {
        this.title = req.getTitle();
        this.content = req.getContent();
    }
}

package com.ureca.juksoon.domain.feed.entity;

import com.ureca.juksoon.domain.common.BaseEntity;
import com.ureca.juksoon.domain.feed.dto.request.CreateFeedReq;
import com.ureca.juksoon.domain.feed.dto.request.ModifyFeedReq;
import com.ureca.juksoon.domain.store.entity.Store;
import com.ureca.juksoon.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@Table(name = "feed", indexes = {
        @Index(name = "idx_price_id", columnList = "price, id")
})
@NoArgsConstructor
@AllArgsConstructor
public class Feed extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private Category category;

    @Column(name = "price")
    private int price;

    @Column(name = "max_user")
    private int maxUser;

    @Column(name = "registered_user")
    private int registeredUser;

    @Column(name = "start_at")
    private String startAt;

    @Column(name = "expired_at")
    private String expiredAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    public static Feed of(CreateFeedReq req, User user, Store store) {
        return Feed.builder()
                .title(req.getTitle())
                .content(req.getContent())
                .category(req.getCategory())
                .price(req.getPrice())
                .maxUser(req.getMaxUser())
                .registeredUser(0)
                .startAt(req.getStartAt())
                .expiredAt(req.getExpiredAt())
                .status(Status.UPCOMING)
                .store(store)
                .user(user)
                .build();
    }

    public void update(ModifyFeedReq req) {
        this.title = req.getTitle();
        this.content = req.getContent();
        this.expiredAt = req.getExpiredAt();
        this.category = req.getCategory();
        this.price = req.getPrice();
    }

    public void updateRegisteredUser(int registeredUser) {
        this.registeredUser = registeredUser;
    }

    public void increaseRegisterUser() {
        this.registeredUser++;
    }
}

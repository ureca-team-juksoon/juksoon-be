package com.ureca.juksoon.domain.feed.dto.response;

import com.ureca.juksoon.domain.feed.entity.Category;
import com.ureca.juksoon.domain.feed.entity.Feed;
import com.ureca.juksoon.domain.feed.entity.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Schema(description = "피드 상세 조회")
@Getter
public class GetFeedDetailRes {

    @Schema(description = "피드 id", example = "1")
    private Long id;

    @Schema(description = "제목", example = "[모집] 햄버거 드실 분")
    private String title;

    @Schema(description = "내용", example = "햄버거 맛있어요")
    private String content;

    @Schema(description = "카테고리", example = "KOREAN")
    private Category category;

    @Schema(description = "가격", example = "1200")
    private int price;

    @Schema(description = "최대 신청 인원", example = "20")
    private int maxUser;

    @Schema(description = "현재 신청 인원", example = "5")
    private int registeredUser;

    @Schema(description = "예약 시작 시간", example = "2025-05-30")
    private String startAt;

    @Schema(description = "방문 예정 날짜", example = "2025-06-30")
    private String expiredAt;

    @Schema(description = "신청 가능 상태", example = "UPCOMING")
    private Status status;

    @Schema(description = "이미지 리스트 Url", example = "링크, 링크")
    private List<String> imageUrlList;

    @Schema(description = "영상 Url", example = "링크")
    private String videoUrl;

    @Schema(description = "가게 이름", example = "빽다방 선릉중앙점")
    private String storeName;

    @Schema(description = "가게 주소", example = "서울특별시 강남구")
    private String address;

    @Schema(description = "가게 설명", example = "커피가 싸고 맛있어요!")
    private String description;

    @Schema(description = "로고 이미지 Url", example = "로고 링크")
    private String logoImageUrl;

    public GetFeedDetailRes(Feed feed, List<String> imageUrlList, String videoUrl) {
        this.id = feed.getId();
        this.title = feed.getTitle();
        this.content = feed.getContent();
        this.category = feed.getCategory();
        this.price = feed.getPrice();
        this.maxUser = feed.getMaxUser();
        this.registeredUser = feed.getRegisteredUser();
        this.startAt = feed.getStartAt();
        this.expiredAt = feed.getExpiredAt();
        this.status = feed.getStatus();
        this.imageUrlList = imageUrlList;
        this.videoUrl = videoUrl;
        this.storeName = feed.getStore().getName();
        this.address = feed.getStore().getAddress();
        this.description = feed.getStore().getDescription();
        this.logoImageUrl = feed.getStore().getLogoImageURL();
    }
}

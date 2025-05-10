package com.ureca.juksoon.domain.feed.dto.responce;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ureca.juksoon.domain.feed.entity.Feed;
import com.ureca.juksoon.domain.feed.entity.Status;
import com.ureca.juksoon.domain.user.entity.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "피드 카드 정보")
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetFeedRes {
    @JsonInclude(JsonInclude.Include.ALWAYS)
    @Schema(description = "피드 id", example = "1")
    private Long feedId;
    @Schema(description = "제목", example = "[모집] 햄버거 드실 분")
    private String title;
    @Schema(description = "가격", example = "1200") // Optional
    private Integer price;
    @Schema(description = "가게 이름", example = "빽다방 선릉중앙점") // Optional
    private String storeName;
    @Schema(description = "최대 신청 인원", example = "20")
    private int maxUser;
    @Schema(description = "현재 신청 인원", example = "5")
    private int registeredUser;
    @Schema(description = "예약 시작일", example = "2025-05-30")
    private String startAt;
    @Schema(description = "신청 마감일", example = "2025-06-30")
    private String expiredAt;
    @Schema(description = "상태", example = "OPEN")
    private Status status;

    public GetFeedRes(Feed feed, UserRole role) {
        this.feedId = feed.getId();
        this.title = feed.getTitle();
        this.maxUser = feed.getMaxUser();
        this.registeredUser = feed.getRegisteredUser();
        this.startAt = feed.getStartAt();
        this.expiredAt = feed.getExpiredAt();
        this.status = feed.getStatus();

        if(role != UserRole.ROLE_OWNER) { // 사장은 표기X
            this.price = feed.getPrice();
            this.storeName = feed.getStore().getName();
        }
    }
}

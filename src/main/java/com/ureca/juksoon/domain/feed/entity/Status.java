package com.ureca.juksoon.domain.feed.entity;

import lombok.Getter;

@Getter
public enum Status {
    // 시작안함, 신청 가능, 기간 만료
    UPCOMING, OPEN, CLOSED
}

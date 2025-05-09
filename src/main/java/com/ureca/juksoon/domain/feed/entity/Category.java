package com.ureca.juksoon.domain.feed.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {
    KOREAN("한식"),
    JAPANESE("일식"),
    WESTERN("양식"),
    CHINESE("중식"),
    BUNSIK("분식"),
    DESSERT("디저트/카페")
    ;

    private final String name;
}


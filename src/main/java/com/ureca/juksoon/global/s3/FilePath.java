package com.ureca.juksoon.global.s3;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FilePath {
    Feed("feed/"),
    REVIEW("review/"),
    STORE("store/");

    private final String path;
}

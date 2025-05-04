package com.ureca.juksoon.global.security.oauth.userdetail;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PrincipalKey {
    USER_ID("userId"),
    USER_ROLE("role"),
    ;

    private final String key;
}

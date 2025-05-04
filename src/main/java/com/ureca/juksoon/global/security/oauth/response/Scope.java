package com.ureca.juksoon.global.security.oauth.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Scope {
    KAKAO_ID_SCOPE(""),
    KAKAO_NAME_SCOPE("profile_nickname"),
    KAKAO_EMAIL_SCOPE("account_email");

    private final String scopeKey;
}

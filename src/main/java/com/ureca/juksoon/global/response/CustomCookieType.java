package com.ureca.juksoon.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Security에서 사용할 Header의 타입을 커스텀해준다.
 * 몇몇은 이미 정의되어있지만, 알아보기 쉽게 일부러 만들어줬다.
 */
@Getter
@RequiredArgsConstructor
public enum CustomCookieType {
    AUTHORIZATION("Authorization"),
    REFRESH_TOKEN("Refresh_Token"),
    ;


    private final String value;
}

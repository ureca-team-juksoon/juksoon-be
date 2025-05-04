package com.ureca.juksoon.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * USER_TESTER : 고객(테스터, 일반) 유저
 * USER_OWNER : 사장 유저
 * USER_ANONYMOUS : 첫 로그인 시 권한 선택이 안돼있는 유저
 */

@Getter
@RequiredArgsConstructor
public enum UserRole {
    ROLE_TESTER("ROLE_TESTER"),
    ROLE_OWNER("ROLE_OWNER"),
    ROLE_FIRST_LOGIN("ROLE_FIRST_LOGIN");

    private final String userRole;
}

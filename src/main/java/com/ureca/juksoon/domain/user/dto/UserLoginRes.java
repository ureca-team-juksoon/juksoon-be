package com.ureca.juksoon.domain.user.dto;

import com.ureca.juksoon.domain.user.entity.UserRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserLoginRes {
    private UserRole userRole;
    private String nickname;
}

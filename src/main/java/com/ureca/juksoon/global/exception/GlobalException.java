package com.ureca.juksoon.global.exception;

import com.ureca.juksoon.global.response.ResultCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GlobalException extends RuntimeException {
    private final ResultCode resultCode;
}

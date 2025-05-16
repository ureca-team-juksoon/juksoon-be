package com.ureca.juksoon.global.event.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageConsumeEvent {
    private String key;
    private Long userId;
}
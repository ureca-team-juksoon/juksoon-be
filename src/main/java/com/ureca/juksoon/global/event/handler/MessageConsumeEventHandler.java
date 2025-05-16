package com.ureca.juksoon.global.event.handler;

import com.ureca.juksoon.global.event.event.MessageConsumeEvent;
import com.ureca.juksoon.global.redis.ordinal.OrdinalRedisExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageConsumeEventHandler {
    private final OrdinalRedisExecutor<Void> ordinalRedisExecutor;

    public void removeTicketRecord(MessageConsumeEvent messageConsumeEvent) {
        ordinalRedisExecutor.rem(messageConsumeEvent.getKey(), messageConsumeEvent.getUserId());
    }
}

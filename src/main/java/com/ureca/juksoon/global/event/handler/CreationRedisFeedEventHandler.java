package com.ureca.juksoon.global.event.handler;

import com.ureca.juksoon.global.event.event.CreationFeedEvent;
import com.ureca.juksoon.global.redis.ordinal.OrdinalRedisExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 레디스에 티켓 퍼블리셔 추가.
 */
@Component
@RequiredArgsConstructor
public class CreationRedisFeedEventHandler {
    private static final String META = ":meta";

    private final OrdinalRedisExecutor<Void> ordinalRedisExecutor;
    public void makeTicketPublisher(CreationFeedEvent creationFeedEvent) {
        String key = creationFeedEvent.getFeedId() + META;
        ordinalRedisExecutor.hset(key, creationFeedEvent);
        ordinalRedisExecutor.setExpiredTime(key, creationFeedEvent.getEndTime());
    }
}

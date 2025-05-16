package com.ureca.juksoon.global.redis.lua;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class LuaScriptExecutor {
    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<String> ticketPublishScript;

    /**
     * feedId 를 KEYS[1], userId 를 ARGV[1] 로 전달하여
     * EVALSHA 호출
     */
    public String issueTicket(String feedId, String userId) {
        return redisTemplate.execute(
                ticketPublishScript,
                Collections.singletonList(feedId),
                userId
        );
    }
}

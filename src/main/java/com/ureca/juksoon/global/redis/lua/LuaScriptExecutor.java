package com.ureca.juksoon.global.redis.lua;

import com.ureca.juksoon.global.redis.lua.RedisLuaScriptFactory;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

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

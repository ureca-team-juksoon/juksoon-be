package com.ureca.juksoon.global.redis.lua;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class RedisLuaConfig {
    private final RedisTemplate<String, String> redisTemplate;

    @Bean
    public DefaultRedisScript<String> ticketPublishScript() {
        DefaultRedisScript<String> script = new DefaultRedisScript<>();
        script.setScriptSource(new ResourceScriptSource(
                        new ClassPathResource("redis-script/ticket-publish-script.lua")
                )
        );
        script.setResultType(String.class);
        return script;
    }

    @Bean
    public String postIssueTicket(){
            return redisTemplate.execute(
                    ticketPublishScript(),
                    Collections.singletonList("-1"),
                    "-1"
            );
    }
}
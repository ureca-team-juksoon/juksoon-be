package com.ureca.juksoon.domain.reservation.service.publisher.executor;

import com.ureca.juksoon.domain.reservation.service.factory.RedisLuaScriptFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LuaScriptExecutor {
    private final RedisLuaScriptFactory redisLuaScriptFactory;
    private final StringRedisTemplate redisTemplate;

    public String execute(String scriptSourceLocation, List<String> keys, Object... args){
        RedisScript<String> redisScript = redisLuaScriptFactory.generateScript(scriptSourceLocation, String.class);
        return redisTemplate.execute(redisScript, keys, args);
    }
}

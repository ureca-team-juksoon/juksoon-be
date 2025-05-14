package com.ureca.juksoon.global.redis.lua;

import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.ScriptSource;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class RedisLuaScriptFactory {
    public <T> RedisScript<T> generateScript(String classPath, Class<T> clazz){
        ScriptSource scriptSource = new ResourceScriptSource(new ClassPathResource(classPath));
        return getRedisScript(scriptSource, clazz);
    }

    private <T> DefaultRedisScript<T> getRedisScript(ScriptSource scriptSource, Class<T> tClass){
        DefaultRedisScript<T> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(scriptSource);
        redisScript.setResultType(tClass);
        return redisScript;
    }
}

package com.ureca.juksoon.global.redis.ordinal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Json으로 된 데이터를 레디스에 처리한다.
 * 직렬화 로직을 동적으로 활용한다.
 * 해시를 사용한다.
 */
@Component
@RequiredArgsConstructor
public class OrdinalRedisExecutor<T> {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    //Key를 받아와서 Json으로 반환 => key와 역직렬화 시킬 클래스
    public T hget(String key, Class<T> targetClazz){
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        if (entries == null || entries.isEmpty()) {
            return null;
        }

        Map<String, Object> valueMap = new HashMap<>();
        for (Map.Entry<Object, Object> entry : entries.entrySet()) {
            valueMap.put(entry.getKey().toString(), parseValue(entry.getValue()));
        }
        return objectMapper.convertValue(valueMap, targetClazz);
    }

    public void hset(String key, Object values) {
        Map<String, Object> rawMap = objectMapper.convertValue(values, new TypeReference<>() {});
        Map<String, String> stringMap = new HashMap<>();
        rawMap.forEach((field, val) -> {
            try {
                stringMap.put(field, objectMapper.writeValueAsString(val));
            } catch (JsonProcessingException e) {
                stringMap.put(field, val != null ? val.toString() : null);
            }
        });
        redisTemplate.opsForHash().putAll(key, stringMap);
    }

    public void rem(String key, Long userId){
        redisTemplate.opsForSet().remove(key, userId.toString());
    }

    public void setExpiredTime(String key, String endTime) {
        LocalDateTime ldt = LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Instant instant = ldt.atZone(ZoneId.systemDefault()).toInstant();

        redisTemplate.expireAt(key, Date.from(instant));
    }

    private Object parseValue(Object raw) {
        String str = raw != null ? raw.toString() : null;
        if (str == null) {
            return null;
        }
        try {
            return objectMapper.readValue(str, Object.class);
        } catch (JsonProcessingException e) {
            return str;
        }
    }
}

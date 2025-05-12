package com.ureca.juksoon.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisConnectionFactory lettuceConnectionFactory() {
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration("127.0.0.1", 6379));
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(lettuceConnectionFactory());
        setTemplate(template, new StringRedisSerializer());
        return template;
    }

    //script들을 불러오는데, I/O를 실시하기 때문에, 런타임 시점에, 미리 메모리에 적재해둔다.
    //@Bean
    //public <T> RedisScript<T> loadLuaRedisScript(){
    //}

    private void setTemplate(RedisTemplate<String, String> template, StringRedisSerializer serializer){
        template.setKeySerializer(serializer);
        template.setHashKeySerializer(serializer);
        template.setHashValueSerializer(serializer);
        template.setValueSerializer(serializer);
        template.afterPropertiesSet();
    }
}

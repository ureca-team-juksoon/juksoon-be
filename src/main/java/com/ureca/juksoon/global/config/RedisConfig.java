package com.ureca.juksoon.global.config;

import io.lettuce.core.ClientOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import io.lettuce.core.SocketOptions;

import java.time.Duration;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private int port;

    @Bean
    public RedisConnectionFactory lettuceConnectionFactory() {

        // 1) Standalone 설정
        RedisStandaloneConfiguration serverConfig = new RedisStandaloneConfiguration(host, port);
        // 2) 클라이언트 옵션
        ClientOptions clientOptions = ClientOptions.builder()
                .autoReconnect(true)// 네트워크 끊김 발생 시 자동 재접속
                .socketOptions(SocketOptions.builder()
                        .keepAlive(true)
                        .connectTimeout(Duration.ofSeconds(10))
                        .build())
                .build();
        // 3) 넷티 옵션 (optional)
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .clientOptions(clientOptions)
                .commandTimeout(Duration.ofSeconds(60))  // 명령 타임아웃
                .shutdownTimeout(Duration.ofMillis(100))
                .build();
        return new LettuceConnectionFactory(serverConfig, clientConfig);
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

package com.ureca.juksoon.domain.reservation.service.consumer;

import java.time.Duration;

import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer.StreamMessageListenerContainerOptions;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 폴링 방식을 이용하여 redis Streams에서 데이터를 꺼내온다.
 * 설정을 이곳에서 해준다.
 * 소비 쓰레드 풀이랑, 배치 사이즈 적절히 조절하는 방법 공부 후 리팩터링 할 것.
 */

@Slf4j
@Configuration
@EnableScheduling               //폴링 루프를 위한 스케쥴링
@RequiredArgsConstructor
public class ReservationStreamConsumerConfig {
    private static final String STREAM_KEY = "feed:stream";
    private static final String GROUP_NAME = "reservation";

    private final RedisTemplate<String, String> redisTemplate;

    @Bean
    public StreamMessageListenerContainer<String, MapRecord<String,String,String>> container(
            RedisConnectionFactory cf,
            ReservationStreamConsumer reservationConsumer) {

        StreamMessageListenerContainer<String, MapRecord<String, String, String>> container = container(cf, getOptions());
        initConsumerGroup();
        container.receive( //컨테이너 스트림 정보, 설정 세팅
                Consumer.from(GROUP_NAME, "reservationConsumer"),
                StreamOffset.create(STREAM_KEY, ReadOffset.lastConsumed()),
                reservationConsumer
        );
        container.start();  //폴링 루프 시작
        return container;
    }

    /**
     * 폴 루프를 도는 쓰레드 풀들을 생성한다.
     * 테스트를 진행하며, 맞춰주도록 하자.
     */
    @Bean
    public ThreadPoolTaskExecutor executor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        return executor;
    }


    private void initConsumerGroup() {
        StreamOperations<String, String, String> ops = redisTemplate.opsForStream();
        ops.add(STREAM_KEY, Collections.singletonMap("init", "1"));
        try {
            ops.destroyGroup(STREAM_KEY, GROUP_NAME);
        } catch (Exception ignore) { }
        ops.createGroup(STREAM_KEY, ReadOffset.latest(), GROUP_NAME);
    }


    private StreamMessageListenerContainer<String, MapRecord<String, String, String>> container(
            RedisConnectionFactory RedisConnectionFactory,
            StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options) {

        return StreamMessageListenerContainer.create(RedisConnectionFactory, options);
    }

    private StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> getOptions() {
        return StreamMessageListenerContainerOptions.builder()
                .batchSize(100)                      //최대 몇 개씩 Polling 할 것이냐? 조각화를 위해 나중에 리팩터링 해줄 것.
                .pollTimeout(Duration.ofSeconds(1))              //하나의 쓰레드를 블록하고, 메시지를 기다린다. 메시지 왔을 시 바로 반환
                .executor(executor())
                .build();
    }
}

package com.ureca.juksoon.domain.reservation.service.consumer;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

/**
 * 스트림즈에 들어가있는 티켓들을 ReservationStreamConfig 여기서 받아
 * onMessage로 던져준다.
 * 100개 가져온 티켓들을
 * 개별의 처리 로직으로 둔다.
 * 그 뭐냐 배치처리가 더 효율적일 것 같은데,,,
 */

@Component
@RequiredArgsConstructor
public class ReservationConsumer implements StreamListener<String, MapRecord<String, String, String>> {
    private final RedisTemplate<String, String> redisTemplate;
    private final ReservationConsumerService reservationConsumerService;

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        Map<String, String> body = message.getValue();
        Long feedId = Long.valueOf(body.get("feedId"));
        Long userId = Long.valueOf(body.get("userId"));
        Integer currentTicketCount = Integer.parseInt(body.get("currentTicketCount"));
        Integer maxTicketCount = Integer.parseInt(body.get("maxTicketCount"));

        reservationConsumerService.processReservationMessage(userId, feedId, currentTicketCount);

        redisTemplate.opsForStream().acknowledge("feed:stream", "reservation", message.getId());
    }

}

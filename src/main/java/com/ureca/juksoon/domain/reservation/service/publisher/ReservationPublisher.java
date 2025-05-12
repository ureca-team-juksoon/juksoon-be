package com.ureca.juksoon.domain.reservation.service.publisher;

import com.ureca.juksoon.domain.reservation.dto.ReservationReq;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 여기서 톰캣 워커 쓰레드의 일은 끝난다.
 * 그냥 레디스로 건내주고, 쓰레드 반납
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationPublisher {
    private static final String STREAM_MESSAGE_KEY = "feed:stream";

    private final StringRedisTemplate redisTemplate;
    private final TicketPublisher ticketPublisher;

    public Ticket publishTicket(Long userId, ReservationReq reservationReq) {
        Ticket ticket = ticketPublisher.publish(userId, reservationReq);   //티켓 발행
        if(!hasErr(ticket)) {  //에러 시 티켓 바로 반환(err만 채워져 있음)
            return ticket;
        }

        sendRedisStreamEvent(ticket);   //레디스 스트림 보내기
        return ticket;
    }

    private boolean hasErr(Ticket ticket){
        return ticket.getErr() != null;
    }

    private void sendRedisStreamEvent(Ticket ticket) {
        redisTemplate.opsForStream().add(getRecord(ticket.convertStreamMessageBody()));
    }

    private MapRecord<String, String, String> getRecord(Map<String, String> streamMessageBody) {
        return StreamRecords.mapBacked(streamMessageBody).withStreamKey(STREAM_MESSAGE_KEY);
    }
}

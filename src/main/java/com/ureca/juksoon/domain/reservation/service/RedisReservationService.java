package com.ureca.juksoon.domain.reservation.service;

import com.ureca.juksoon.domain.reservation.service.publisher.ReservationStreamPublisher;
import com.ureca.juksoon.domain.reservation.service.publisher.Ticket;
import com.ureca.juksoon.domain.reservation.service.publisher.TicketPublisher;
import com.ureca.juksoon.global.exception.GlobalException;
import com.ureca.juksoon.global.response.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 이곳에서 재현님의 코드와 분기가 갈린다.
 * 만약, reserve()가 제대로 동작하지 않는다면, 재현님 코드로
 *
 * 티켓을 발급받는다.
 * 티켓을 스트림으로 보낸다.(비동기 시작)
 * 에러 혹은 정보가 들어있는 티켓을 return 한다.
 */
@Service
@RequiredArgsConstructor
public class RedisReservationService {
    private final TicketPublisher ticketPublisher;
    private final ReservationStreamPublisher reservationStreamPublisher;

    public Ticket reserve(Long userId, Long feedId) {
        Ticket ticket = ticketPublisher.publish(userId, feedId);

        if(ticket.hasError()) throwError(ticket);

        reservationStreamPublisher.sendToStream(ticket);
        return ticket;
    }

    private void throwError(Ticket ticket) {
        switch (ticket.getErr()){
            case 1 -> throw new GlobalException(ResultCode.TICKET_PUBLISHER_NOT_EXISTS);
            case 2 -> throw new GlobalException(ResultCode.TICKET_ALREADY_PUBLISHED);
            case 3 -> throw new GlobalException(ResultCode.CLOSED_FEED);
        }
    }
}

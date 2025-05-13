package com.ureca.juksoon.domain.reservation.service;

import com.ureca.juksoon.domain.reservation.dto.ReservationReq;
import com.ureca.juksoon.domain.reservation.service.publisher.ReservationPublisher;
import com.ureca.juksoon.domain.reservation.service.publisher.Ticket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 이곳에서 재현님의 코드와 분기가 갈린다.
 * 만약, reserve()가 제대로 동작하지 않는다면, 재현님 코드로
 */
@Service
@RequiredArgsConstructor
public class RedisReservationService {
    private final ReservationPublisher reservationPublisher;

    /**
     * 사용자가 요청을 보내면 이 메서드로 첫 번째로 진입한다.
     * 생산자에서 티켓이 성공적으로 발급되면, 바로 return해줘 응답을 보내준다.
     * 생산자에서 티켓이 성공적으로 발급되지 않는다면, 바로 return 해줘 에러를 보내준다.
     */
    public Ticket reserve(Long userId, ReservationReq reservationReq) {

        return reservationPublisher.publishTicket(userId, reservationReq);
    }
}

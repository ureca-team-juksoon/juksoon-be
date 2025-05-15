package com.ureca.juksoon.domain.reservation.service.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 티켓을 발급 받고 스트림을 통해 소비가 되었음에도, 에러가 나는 경우 처리
 * 비동기 예외 처리 전략 공부 후 코드 추가할 것.
 */
@Slf4j
@Component
public class ReservationStreamConsumerExceptionHandler {
    public void handleErrorTicket(Long userId, Long feedId, Integer currentTicketCount) {
    }
}

package com.ureca.juksoon.domain.reservation.service.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ReservationConsumerExceptionHandler {
    public void handleErrorTicket(Long userId, Long feedId, Integer currentTicketCount) {
        //일단 냅두자. 지금 생각할 단계가 아니다 이 로직은 개어렵다.
    }
}

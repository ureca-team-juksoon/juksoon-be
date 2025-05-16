package com.ureca.juksoon.domain.reservation.service.consumer;

import com.ureca.juksoon.domain.reservation.service.consumer.exception.ConsumerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 티켓을 발급 받고 스트림을 통해 소비가 되었음에도, 에러가 나는 경우 처리
 * 비동기 예외 처리 전략 공부 후 코드 추가할 것.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationStreamConsumerExceptionService {
    private final ReservationExceptionRepository reservationExceptionRepository;

    @Transactional
    public void saveException(Exception e, Long userId, Long feedId, Integer currentTicketCount) {
        reservationExceptionRepository.save(
                ConsumerException.builder()
                        .feedId(feedId)
                        .userId(userId)
                        .currentTicketCount(currentTicketCount)
                        .errMsg(e.getMessage())
                        .build());
    }
}

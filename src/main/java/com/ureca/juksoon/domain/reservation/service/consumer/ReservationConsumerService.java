package com.ureca.juksoon.domain.reservation.service.consumer;

import com.ureca.juksoon.domain.feed.entity.Feed;
import com.ureca.juksoon.domain.feed.repository.FeedRepository;
import com.ureca.juksoon.domain.reservation.entity.Reservation;
import com.ureca.juksoon.domain.reservation.repository.ReservationRepository;
import com.ureca.juksoon.domain.user.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 메시지 처리
 * 피드 존재 X 시 예외 처리
 * 피드 존재 시 정상 플로우
 * 등록 유저 업데이트
 * 새 예약 저장
 */

@Service
@RequiredArgsConstructor
public class ReservationConsumerService {
    private final ReservationConsumerExceptionHandler reservationConsumerExceptionHandler;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final FeedRepository feedRepository;

    //예외 처리 들어가야함
    @Transactional
    public void processReservationMessage(Long userId, Long feedId, Integer currentTicketCount){
        Optional<Feed> feed = feedRepository.findById(feedId);
        if(feed.isPresent()){
            feed.get().updateRegisteredUser(currentTicketCount);
            Reservation newReservation = makeNewReservation(userId, feedId);
            reservationRepository.save(newReservation);
        }

        reservationConsumerExceptionHandler.handleErrorTicket(userId, feedId, currentTicketCount);
    }

    private Reservation makeNewReservation(Long userId, Long feedId) {
        return Reservation.builder()
                .user(userRepository.getReferenceById(userId))
                .feed(feedRepository.getReferenceById(feedId))
                .build();
    }
}

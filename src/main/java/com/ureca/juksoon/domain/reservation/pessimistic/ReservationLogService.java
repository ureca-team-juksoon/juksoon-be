package com.ureca.juksoon.domain.reservation.pessimistic;

import com.ureca.juksoon.domain.reservation.entity.Reservation;
import com.ureca.juksoon.domain.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationLogService {

    private final ReservationRepository reservationRepository;

    @Async
    public void saveFailReservation(Reservation reservation) {
        reservationRepository.save(reservation);
    }
}

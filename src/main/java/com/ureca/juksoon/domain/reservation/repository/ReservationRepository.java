package com.ureca.juksoon.domain.reservation.repository;

import com.ureca.juksoon.domain.reservation.entity.Reservation;
import com.ureca.juksoon.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findAllByUser(User user);
}

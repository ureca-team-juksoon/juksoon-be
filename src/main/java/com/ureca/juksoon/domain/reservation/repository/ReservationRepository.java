package com.ureca.juksoon.domain.reservation.repository;

import com.ureca.juksoon.domain.feed.entity.Feed;
import com.ureca.juksoon.domain.reservation.entity.Reservation;
import com.ureca.juksoon.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    boolean existsReservationByUserAndFeed(User user, Feed feed);
}

package com.ureca.juksoon.domain.reservation.repository;

import com.ureca.juksoon.domain.feed.entity.Feed;
import com.ureca.juksoon.domain.reservation.entity.Reservation;
import com.ureca.juksoon.domain.reservation.entity.ReservationAttemptState;
import com.ureca.juksoon.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findAllByStateAndFeed_Id(ReservationAttemptState state, Long feedId);

    boolean existsReservationByUserAndStateAndFeed(User user, ReservationAttemptState state, Feed feed);
}

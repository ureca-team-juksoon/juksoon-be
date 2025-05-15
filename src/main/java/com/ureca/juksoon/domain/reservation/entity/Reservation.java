package com.ureca.juksoon.domain.reservation.entity;

import com.ureca.juksoon.domain.feed.entity.Feed;
import com.ureca.juksoon.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "reservation")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "feed_id")
    private Feed feed;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private ReservationAttemptState state;

    @Column(name = "attempted_at")
    private LocalDateTime attemptedAt;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    public static Reservation of(Feed feed, User user, ReservationAttemptState state, LocalDateTime requestTime) {
        return Reservation.builder()
                .feed(feed)
                .user(user)
                .state(state)
                .attemptedAt(requestTime)
                .build();
    }

    public void cancelReservation(LocalDateTime requestTIme){
        this.canceledAt = requestTIme;
        this.state = ReservationAttemptState.CANCELED_BY_USER;
    }
}

package com.ureca.juksoon.domain.reservation.dto;

import com.ureca.juksoon.domain.reservation.entity.Reservation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationRes {
    private Long reservationId;

    public static ReservationRes toEntity(Reservation reservation){
        return ReservationRes.builder()
                .reservationId(reservation.getId())
                .build();
    }
}

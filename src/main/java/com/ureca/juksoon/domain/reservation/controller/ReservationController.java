package com.ureca.juksoon.domain.reservation.controller;

import com.ureca.juksoon.domain.reservation.dto.ReservationReq;
import com.ureca.juksoon.domain.reservation.service.RedisReservationService;
import com.ureca.juksoon.global.response.CommonResponse;
import com.ureca.juksoon.global.security.jwt.userdetail.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReservationController {
    private final RedisReservationService reservationService;

    @PostMapping("/reservation")
    public CommonResponse<?> reserve(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ReservationReq reservationReq
    ) {
        return CommonResponse.success(reservationService.reserve(userDetails.getUserId(), reservationReq));
    }
}

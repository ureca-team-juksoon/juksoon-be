package com.ureca.juksoon.domain.reservation.test;

import com.ureca.juksoon.domain.reservation.service.RedisReservationService;
import com.ureca.juksoon.global.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReservationControllerTest {
    private final RedisReservationService reservationService;

    @PostMapping("/reservation/test")
    public CommonResponse<?> reserve(
            @RequestBody ReservationTestReq reservationReq
    ) {
        return CommonResponse.success(reservationService.reserve(reservationReq.getUserId(), reservationReq.getFeedId()));
    }
}

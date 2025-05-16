package com.ureca.juksoon.domain.reservation.controller;

import com.ureca.juksoon.domain.reservation.dto.ReservationReq;
import com.ureca.juksoon.domain.reservation.service.RedisReservationService;
import com.ureca.juksoon.global.response.CommonResponse;
import com.ureca.juksoon.global.security.jwt.userdetail.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReservationController {
    private final RedisReservationService reservationService;

    /**
     * 예약 하기
     * @param userDetails 사용자 정보
     * @param reservationReq 피드 생성을 위한 정보
     */
    @Operation(summary = "예약 하기", description = "예약 하기")
    @PostMapping("/reservation")
    public CommonResponse<?> reserve(
            @Parameter(description = "사용자정보", required = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "예약 feed Id", required = true)
            @RequestBody ReservationReq reservationReq
    ) {
        return CommonResponse.success(reservationService.reserve(userDetails.getUserId(), reservationReq.getFeedId()));
    }
}

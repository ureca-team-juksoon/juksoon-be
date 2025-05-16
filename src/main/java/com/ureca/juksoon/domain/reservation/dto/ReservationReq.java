package com.ureca.juksoon.domain.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReservationReq {
    @Schema(description = "피드 id", example = "티켓을 발급받기 위한 feed id를 받는다.")
    private Long feedId;
}

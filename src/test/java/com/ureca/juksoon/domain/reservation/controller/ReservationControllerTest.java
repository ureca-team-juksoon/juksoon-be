package com.ureca.juksoon.domain.reservation.controller;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,  // 또는 RANDOM_PORT
        classes = ReservationController.class
)
@ActiveProfiles("test")  // 필요하면 테스트용 프로파일
public class ReservationControllerTest {

}

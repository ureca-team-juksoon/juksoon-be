package com.ureca.juksoon.domain.reservation.pessimistic;

import com.ureca.juksoon.domain.reservation.entity.Reservation;
import com.ureca.juksoon.domain.reservation.repository.ReservationRepository;
import com.ureca.juksoon.domain.user.entity.UserRole;
import com.ureca.juksoon.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@EnableAsync
class PessimisticReservationServiceTest {

    @Autowired
    private PessimisticReservationService service;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    public void 비관락_테스트() throws Exception {

        final int threadCount = 33; //동시 요청 수 , DB 내에 사람 수 만큼
        final CountDownLatch latch = new CountDownLatch(1);
        final CountDownLatch complete = new CountDownLatch(threadCount);
        final List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<>());

        for (int i = 1; i <= threadCount; i++) {
            final int userId = i;
            new Thread(() -> {
                try {
                    latch.await(); // 모든 스레드가 대기
                    // 선착순 구매 로직 호출
                    service.doReservation(1L, (long) userId);
                } catch (Exception e) {
                    exceptions.add(e);
                } finally {
                    complete.countDown();
                }
            }).start();
        }

        latch.countDown();

        complete.await(10, TimeUnit.SECONDS);

//        List<Reservation> all = reservationRepository.findAll();

//        assertEquals(10, all.size());
    }
}
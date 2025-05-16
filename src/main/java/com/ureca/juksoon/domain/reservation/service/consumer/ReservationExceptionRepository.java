package com.ureca.juksoon.domain.reservation.service.consumer;

import com.ureca.juksoon.domain.reservation.service.consumer.exception.ConsumerException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationExceptionRepository extends JpaRepository<ConsumerException, Long> {
}
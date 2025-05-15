package com.ureca.juksoon.domain.reservation.service.consumer.exception;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "consumer_exception")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsumerException {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "feed_id")
    private Long feedId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "current_ticket_count")
    private Integer currentTicketCount;

    @Column(name = "err_msg")
    private String errMsg;
}
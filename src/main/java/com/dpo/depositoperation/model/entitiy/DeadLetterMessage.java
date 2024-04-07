package com.dpo.depositoperation.model.entitiy;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "DEAD_LETTER_MESSAGES")
public class DeadLetterMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "QUEUE")
    private String queue;

    @Column(name = "EXCHANGE")
    private String exchange;

    @Column(name = "MESSAGE")
    private String message;

    @Column(name = "REASON")
    private String reason;

    @Column(name = "DATE_TIME")
    private LocalDateTime date;

    @Column(name = "CORRELATION_ID")
    private String correlationId;
}
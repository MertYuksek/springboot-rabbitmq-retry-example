package com.dpo.depositoperation.model.entitiy;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity(name = "DEPOSIT")
public class Deposit {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "CUSTOMER_ID")
    private Long customerNo;

    @Column(name = "AMOUNT")
    private BigDecimal amount;

    @Column(name = "INTEREST_RATE")
    private BigDecimal interestRate;

    @Column(name = "START_DATE")
    private LocalDateTime startDate;

    @Column(name = "END_DATE")
    private LocalDateTime endDate;
}
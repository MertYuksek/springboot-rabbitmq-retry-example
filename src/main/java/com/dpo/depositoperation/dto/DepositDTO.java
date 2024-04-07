package com.dpo.depositoperation.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class DepositDTO {

    private Long id;

    @NotNull(message = "customerNo cannot be null")
    private Long customerNo;

    @NotNull(message = "amount cannot be null")
    @PositiveOrZero(message = "amount cannot be negative")
    private BigDecimal amount;

    @NotNull(message = "interestRate cannot be null")
    @PositiveOrZero(message = "interestRate cannot be negative")
    private BigDecimal interestRate;

    @NotNull(message = "startDate cannot be null")
    private LocalDateTime startDate;

    @NotNull(message = "endDate cannot be null")
    private LocalDateTime endDate;
}
package com.dpo.depositoperation.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class DocumentRequest {

    private Long contractNo;

    private Long customerNo;

    private BigDecimal amount;
}

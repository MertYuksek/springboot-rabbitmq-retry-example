package com.dpo.depositoperation.api.controller;

import com.dpo.depositoperation.api.request.DepositRequest;
import com.dpo.depositoperation.api.request.DepositResponse;
import com.dpo.depositoperation.dto.DepositDTO;
import com.dpo.depositoperation.service.DepositService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@ResponseStatus(HttpStatus.OK)
@Tag(name = "depositoperation-api")
public class DepositController {

    private final DepositService depositService;

    @PostMapping
    @Operation(description = "Deposit save operation")
    DepositResponse saveDeposit(@Valid @RequestBody DepositRequest depositRequest) throws InterruptedException {
        DepositDTO depositDTO1 = depositRequest.getDepositDTO();
        depositDTO1.setCustomerNo(1L);
        depositService.saveDeposit(depositDTO1);
        return DepositResponse.builder().depositDTO(depositDTO1).build();
    }
}

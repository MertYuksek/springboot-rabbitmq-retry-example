package com.dpo.depositoperation.api.request;

import com.dpo.depositoperation.dto.DepositDTO;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class DepositRequest {

    @NotNull
    @Valid
    private DepositDTO depositDTO;
}

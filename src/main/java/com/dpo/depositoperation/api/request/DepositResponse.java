package com.dpo.depositoperation.api.request;

import com.dpo.depositoperation.dto.DepositDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DepositResponse {
    private DepositDTO depositDTO;
}

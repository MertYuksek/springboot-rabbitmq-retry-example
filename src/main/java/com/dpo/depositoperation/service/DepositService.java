package com.dpo.depositoperation.service;

import com.dpo.depositoperation.dto.DepositDTO;

public interface DepositService {
    DepositDTO saveDeposit(DepositDTO depositDTO) throws InterruptedException;
}

package com.dpo.depositoperation.model.repository;

import com.dpo.depositoperation.model.entitiy.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepositRepository extends JpaRepository<Deposit, Long> {
}

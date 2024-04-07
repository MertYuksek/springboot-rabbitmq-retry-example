package com.dpo.depositoperation.model.repository;

import com.dpo.depositoperation.model.entitiy.DeadLetterMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeadLetterMessageRepository extends JpaRepository<DeadLetterMessage, Long> {
    Optional<DeadLetterMessage> findByCorrelationId(String correlationId);
}

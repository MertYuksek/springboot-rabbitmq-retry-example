package com.dpo.depositoperation.service;

import com.dpo.depositoperation.dto.DepositDTO;
import com.dpo.depositoperation.event.publisher.DocumentPublisher;
import com.dpo.depositoperation.mapper.DepositMapper;
import com.dpo.depositoperation.model.entitiy.Deposit;
import com.dpo.depositoperation.model.repository.DepositRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class DepositServiceImpl implements DepositService {
    private final DepositMapper depositMapper;
    private final DepositRepository depositRepository;
    private final DocumentPublisher documentPublisher;

    @Override
    public DepositDTO saveDeposit(DepositDTO depositDTO) throws InterruptedException {
        Deposit deposit = depositMapper.toEntity(depositDTO);
        sendRequestToDocumentGenerationService(deposit);
        return depositMapper.toDTO(deposit);
    }

    private void sendRequestToDocumentGenerationService(Deposit deposit) throws InterruptedException {
        documentPublisher.publishToDocumentQueue(depositMapper.depositToDocumentRequest(deposit));
    }
}
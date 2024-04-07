package com.dpo.depositoperation.mapper;

import com.dpo.depositoperation.dto.DepositDTO;
import com.dpo.depositoperation.dto.DocumentRequest;
import com.dpo.depositoperation.model.entitiy.Deposit;
import org.mapstruct.Mapper;

@Mapper
public interface DepositMapper {
    DepositDTO toDTO(Deposit entity);
    Deposit toEntity(DepositDTO dto);

    default DocumentRequest depositToDocumentRequest(Deposit deposit) {
        DocumentRequest request = new DocumentRequest();
        request.setAmount(deposit.getAmount());
        request.setCustomerNo(deposit.getCustomerNo());
        request.setContractNo(deposit.getId());
        return request;
    }
}

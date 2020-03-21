package com.softindustry.bank.dto.mapper;

import com.softindustry.bank.dto.TransactionDto;
import com.softindustry.bank.entity.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionDtoMapper implements DtoMapper<Transaction, TransactionDto> {

    @Override
    public TransactionDto map(Transaction from) {
        return TransactionDto.builder()
                .amount(from.getAmount())
                .transactionDate(from.getTransactionTime())
                .transactionId(from.getId())
                .transactionType(from.getTransactionType())
                .build();
    }
}

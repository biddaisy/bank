package com.softindustry.bank.controller.dto.mapper;

import com.softindustry.bank.controller.dto.AccountDto;
import com.softindustry.bank.controller.dto.TransactionDto;
import com.softindustry.bank.entity.Account;
import com.softindustry.bank.entity.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class AccountDtoMapper implements DtoMapper<Account, AccountDto> {

    private final DtoMapper<Transaction, TransactionDto> transactionMapper;

    @Autowired
    public AccountDtoMapper(DtoMapper<Transaction, TransactionDto> transactionMapper) {
        this.transactionMapper = transactionMapper;
    }

    @Override
    public AccountDto map(Account from) {
        return AccountDto.builder()
                .accountId(from.getId())
                .accountStatus(from.getStatus())
                .balance(from.getBalance())
                .currency(from.getCurrency())
                .transactions(from.getTransactions().stream()
                .map(transactionMapper::map).collect(Collectors.toList()))
                .build();
    }
}

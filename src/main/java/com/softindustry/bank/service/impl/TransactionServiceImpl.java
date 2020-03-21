package com.softindustry.bank.service.impl;

import com.softindustry.bank.entity.Account;
import com.softindustry.bank.entity.Transaction;
import com.softindustry.bank.entity.enums.TransactionType;
import com.softindustry.bank.repository.TransactionRepository;
import com.softindustry.bank.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    public Transaction saveTransaction(Account account, BigDecimal amount, TransactionType transactionType) {
        return transactionRepository.save(Transaction.builder()
                .account(account)
                .amount(amount)
                .transactionType(transactionType)
                .build());
    }
}

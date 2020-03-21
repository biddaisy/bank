package com.softindustry.bank.service;

import com.softindustry.bank.entity.Account;
import com.softindustry.bank.entity.Transaction;
import com.softindustry.bank.entity.enums.TransactionType;

import java.math.BigDecimal;

public interface TransactionService {
    Transaction saveTransaction(Account account, BigDecimal amount, TransactionType transactionType);
}

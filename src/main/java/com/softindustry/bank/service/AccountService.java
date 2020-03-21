package com.softindustry.bank.service;

import com.softindustry.bank.entity.Account;
import com.softindustry.bank.exception.AccountNotFoundException;
import com.softindustry.bank.exception.AccountStatusException;
import com.softindustry.bank.exception.NotEnoughFundException;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {

    Account findAccountById(Long accountId) throws AccountNotFoundException;
    List<Account> findAccountsByUserId(Long userId) throws AccountNotFoundException;
    Account deposit(Long accountId, BigDecimal amount) throws AccountNotFoundException, AccountStatusException;
    Account withdraw(Long accountId, BigDecimal amount) throws AccountNotFoundException, NotEnoughFundException, AccountStatusException;
}

package com.softindustry.bank.service.impl;

import com.softindustry.bank.entity.Account;
import com.softindustry.bank.entity.enums.AccountStatus;
import com.softindustry.bank.entity.enums.TransactionType;
import com.softindustry.bank.exception.AccountNotFoundException;
import com.softindustry.bank.exception.AccountStatusException;
import com.softindustry.bank.exception.NotEnoughFundException;
import com.softindustry.bank.repository.AccountRepository;
import com.softindustry.bank.service.AccountService;
import com.softindustry.bank.service.TransactionService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final TransactionService transactionService;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository,
                              TransactionService transactionService) {
        this.accountRepository = accountRepository;
        this.transactionService = transactionService;
    }

    @Override
    @Transactional
    public Account deposit(Long accountId, BigDecimal amount) throws AccountNotFoundException, AccountStatusException{
        Optional<Account> retrievedAccount = accountRepository.findAccountById(accountId);
        Account account = retrievedAccount.orElseThrow(() -> new AccountNotFoundException("Account by id: " + accountId + "not found"));

        if (!isActiveAccount(account)) {
            throw new AccountStatusException("Account is " + account.getStatus());
        }
        BigDecimal amountAfterValidation = validateAmount(amount);
        account.setBalance(account.getBalance().add(amountAfterValidation));
        transactionService.saveTransaction(account, amountAfterValidation, TransactionType.DEPOSIT);
        return accountRepository.save(account);
    }

    @Override
    @Transactional
    public Account withdraw(Long accountId, BigDecimal amount) throws AccountNotFoundException, NotEnoughFundException, AccountStatusException {
        Optional<Account> retrievedAccount = accountRepository.findAccountById(accountId);
        Account account = retrievedAccount
                .orElseThrow(() -> new AccountNotFoundException("Account by id: " + accountId + "not found"));
        BigDecimal amountAfterValidation = validateAmount(amount);

        if (account.getBalance().signum() == 0 || account.getBalance().compareTo(amountAfterValidation) < 0) {
            throw new NotEnoughFundException("Not enough funds in the account");
        } else if (!isActiveAccount(account)) {
            throw new AccountStatusException("Account is " + account.getStatus());
        }
        account.setBalance(account.getBalance().subtract(amountAfterValidation));
        transactionService.saveTransaction(account, amountAfterValidation, TransactionType.WITHDRAW);
        return accountRepository.save(account);
    }

    @Override
    public Account findAccountById(Long accountId) throws AccountNotFoundException {
        return accountRepository.findAccountById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account by id: " + accountId + "not found"));
    }

    @Override
    public List<Account> findAccountsByUserId(Long userId) throws AccountNotFoundException {
        List<Account> accounts = accountRepository.findAccountsByUserId(userId);
        if (!accounts.isEmpty()) {
            return accounts;
        } else {
            throw new AccountNotFoundException("Accounts for user id: " + userId + " not found");
        }
    }

    private BigDecimal validateAmount(BigDecimal amount) {
        return amount == null || amount.signum() <= 0 ? BigDecimal.ZERO : amount;
    }

    private boolean isActiveAccount(Account account) {
        AccountStatus accountStatus = account.getStatus();
        return !AccountStatus.CLOSED.equals(accountStatus) && !AccountStatus.BLOCKED.equals(accountStatus);
    }

}

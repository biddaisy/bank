package com.softindustry.bank.service.impl;

import com.softindustry.bank.entity.Account;
import com.softindustry.bank.entity.Transaction;
import com.softindustry.bank.entity.enums.AccountStatus;
import com.softindustry.bank.entity.enums.TransactionType;
import com.softindustry.bank.exception.AccountNotFoundException;
import com.softindustry.bank.exception.AccountStatusException;
import com.softindustry.bank.exception.NotEnoughFundException;
import com.softindustry.bank.exception.ZeroOrNegativeAmountException;
import com.softindustry.bank.repository.AccountRepository;
import com.softindustry.bank.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public Account deposit(Long accountId, BigDecimal amount)
            throws AccountNotFoundException, AccountStatusException, ZeroOrNegativeAmountException{
        Optional<Account> retrievedAccount = accountRepository.findAccountById(accountId);
        Account account = retrievedAccount.orElseThrow(() -> new AccountNotFoundException("Account by id: " + accountId + "not found"));

        BigDecimal amountAfterValidation = validateAmount(amount);
        if (!isActiveAccount(account)) {
            throw new AccountStatusException("Account is " + account.getStatus());
        }

        account.setBalance(account.getBalance().add(amountAfterValidation));
        account.getTransactions().add(buildAndGetTransaction(account, amountAfterValidation, TransactionType.DEPOSIT));
        return accountRepository.save(account);
    }

    @Override
    @Transactional
    public Account withdraw(Long accountId, BigDecimal amount)
            throws AccountNotFoundException, NotEnoughFundException, AccountStatusException, ZeroOrNegativeAmountException {
        Optional<Account> retrievedAccount = accountRepository.findAccountById(accountId);
        Account account = retrievedAccount.orElseThrow(() -> new AccountNotFoundException("Account by id: " + accountId + "not found"));

        BigDecimal amountAfterValidation = validateAmount(amount);
        if (account.getBalance().signum() == 0 || account.getBalance().compareTo(amountAfterValidation) < 0) {
            throw new NotEnoughFundException("Not enough funds in the account");
        } else if (!isActiveAccount(account)) {
            throw new AccountStatusException("Account is " + account.getStatus());
        }

        account.setBalance(account.getBalance().subtract(amountAfterValidation));
        account.getTransactions().add(buildAndGetTransaction(account, amountAfterValidation, TransactionType.WITHDRAW));
        return accountRepository.save(account);
    }

    @Override
    @Transactional
    public Account findAccountById(Long accountId) throws AccountNotFoundException {
        return accountRepository.findAccountById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account by id: " + accountId + "not found"));
    }

    @Override
    @Transactional
    public List<Account> findAccountsByUserId(Long userId) throws AccountNotFoundException {
        List<Account> accounts = accountRepository.findAccountsByUserId(userId);
        if (!accounts.isEmpty()) {
            return accounts;
        } else {
            throw new AccountNotFoundException("Accounts for user id: " + userId + " not found");
        }
    }

    private BigDecimal validateAmount(BigDecimal amount) throws ZeroOrNegativeAmountException {
        if (amount == null || amount.signum() <= 0 ) {
            throw new ZeroOrNegativeAmountException("Amount equals zero or negative number");
        } else {
            return amount;
        }
    }

    private boolean isActiveAccount(Account account) {
        AccountStatus accountStatus = account.getStatus();
        return !AccountStatus.CLOSED.equals(accountStatus) && !AccountStatus.BLOCKED.equals(accountStatus);
    }

    private Transaction buildAndGetTransaction(Account account, BigDecimal amount, TransactionType transactionType) {
        return Transaction.builder()
                .amount(amount)
                .account(account)
                .transactionType(transactionType)
                .build();
    }

}

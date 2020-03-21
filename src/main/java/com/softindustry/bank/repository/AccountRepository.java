package com.softindustry.bank.repository;

import com.softindustry.bank.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findAccountsByUserId(Long userId);
    Optional<Account> findAccountById(Long accountId);
}

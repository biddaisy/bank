package com.softindustry.bank.controller;

import com.softindustry.bank.controller.dto.AccountDto;
import com.softindustry.bank.controller.dto.mapper.DtoMapper;
import com.softindustry.bank.entity.Account;
import com.softindustry.bank.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class AccountController {

    private static final String AMOUNT_BODY_KEY = "amount";

    private final AccountService accountService;
    private final DtoMapper<Account, AccountDto> accountMapper;

    @Autowired
    public AccountController(AccountService accountService,
                             DtoMapper<Account, AccountDto> accountMapper) {
        this.accountService = accountService;
        this.accountMapper = accountMapper;
    }

    @GetMapping("api/accounts/{id}")
    public ResponseEntity<AccountDto> getAccount(@PathVariable(name = "id") Long accountId) {
        try {
            return ResponseEntity.ok(accountMapper.map(accountService.findAccountById(accountId)));
        } catch (Exception e) {
            log.error("Error occurred while getting account {}", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("api/accounts")
    public ResponseEntity<List<AccountDto>> getAccounts(@RequestParam Long userId) {
        try {
            return ResponseEntity.ok(accountService.findAccountsByUserId(userId).stream()
                    .map(accountMapper::map).collect(Collectors.toList()));
        } catch (Exception e) {
            log.error("Error occurred while getting accounts {}", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("api/accounts/{id}/withdraw")
    public ResponseEntity<AccountDto> withdrawFromBalance(@PathVariable(name = "id") Long accountId,
                                                       @RequestBody Map<String, String> body) {
        try {
            BigDecimal withdrawAmount = new BigDecimal(body.get(AMOUNT_BODY_KEY));
            return ResponseEntity.ok(accountMapper.map(accountService.withdraw(accountId, withdrawAmount)));
        } catch (Exception e) {
            log.error("Error occurred while withdrawing balance {}", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("api/accounts/{id}/deposit")
    public ResponseEntity<AccountDto> depositToBalance(@PathVariable(name = "id") Long accountId,
                                                       @RequestBody Map<String, String> body) {
        try {
            BigDecimal depositAmount = new BigDecimal(body.get(AMOUNT_BODY_KEY));
            return ResponseEntity.ok(accountMapper.map(accountService.deposit(accountId, depositAmount)));
        } catch (Exception e) {
            log.error("Error occurred while depositing balance {}", e);
            return ResponseEntity.badRequest().build();
        }
    }

}

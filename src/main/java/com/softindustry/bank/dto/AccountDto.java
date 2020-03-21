package com.softindustry.bank.dto;

import com.softindustry.bank.entity.enums.AccountStatus;
import com.softindustry.bank.entity.enums.Currency;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class AccountDto {
    private Long accountId;
    private Currency currency;
    private AccountStatus accountStatus;
    private BigDecimal balance;
    private List<TransactionDto> transactions;
}

package com.softindustry.bank.dto;

import com.softindustry.bank.entity.enums.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionDto {
    private Long transactionId;
    private BigDecimal amount;
    private TransactionType transactionType;
    private LocalDateTime transactionDate;
}
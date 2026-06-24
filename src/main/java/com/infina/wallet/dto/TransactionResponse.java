package com.infina.wallet.dto;

import com.infina.wallet.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        BigDecimal amount,
        TransactionType transactionType,
        LocalDateTime transactionDate,
        String description
) {}

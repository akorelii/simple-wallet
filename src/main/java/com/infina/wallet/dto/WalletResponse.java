package com.infina.wallet.dto;

import com.infina.wallet.enums.CurrencyType;

import java.math.BigDecimal;

public record WalletResponse(
        Long id,
        String accountNumber,
        BigDecimal balance,
        CurrencyType currency
) {}
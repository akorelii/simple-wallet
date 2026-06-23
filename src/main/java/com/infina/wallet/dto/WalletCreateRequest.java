package com.infina.wallet.dto;

import com.infina.wallet.enums.CurrencyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record WalletCreateRequest(

    @NotBlank(message = "Account number cannot be blank")
    String accountNumber,

    @NotNull(message = "Currency cannot be null")
    CurrencyType currency
){}

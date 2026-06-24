package com.infina.wallet.dto;

import com.infina.wallet.enums.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record TransactionCreateRequest(
        @NotBlank(message = "Source account number cannot be blank")
        String sourceAccountNumber,

        @NotBlank(message = "Target account number cannot be blank")
        String targetAccountNumber,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than zero")
        private BigDecimal amount
) {
        public TransactionType transactionType() {
        }
}
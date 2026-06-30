package com.infina.wallet.dto; // Eğer doğrudan dto klasöründeyse '.request' kısmını sil.

import com.infina.wallet.enums.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransactionCreateRequest(

        @NotBlank(message = "Kaynak hesap numarası boş bırakılamaz.")
        String sourceAccountNumber,

        /**
         * Burada bilerek @NotBlank kullanmıyoruz.
         * Çünkü DEPOSIT (Para Yatırma) veya WITHDRAW (Para Çekme) işlemlerinde
         * hedef hesap numarasına ihtiyaç yoktur, dışarıdan null gönderilecektir.
         */
        String targetAccountNumber,

        @NotNull(message = "İşlem tutarı zorunludur.")
        @DecimalMin(value = "0.0", inclusive = false, message = "İşlem tutarı sıfırdan büyük olmalıdır.")
        BigDecimal amount,

        @NotNull(message = "İşlem tipi boş bırakılamaz.")
        TransactionType transactionType
) {}
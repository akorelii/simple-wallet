package com.infina.wallet.exception;

import lombok.Getter;

@Getter
public class WalletException extends RuntimeException {

    private final ErrorType errorType;

    // Sadece ErrorType alarak hata fırlatmak için constructor
    public WalletException(ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }

    // Hem ErrorType hem de ek bir mesaj ("Hesap bulunamadı: ACC-123" gibi) fırlatmak için constructor
    public WalletException(ErrorType errorType, String customMessage) {
        super(customMessage);
        this.errorType = errorType;
    }
}
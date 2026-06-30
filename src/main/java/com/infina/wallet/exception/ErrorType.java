package com.infina.wallet.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorType {

    // Cüzdan Hataları (1000'li kodlar) ---
    WALLET_NOT_FOUND(1001, "Aradığınız hesap numarasına ait cüzdan bulunamadı.", HttpStatus.NOT_FOUND),
    WALLET_ALREADY_EXISTS(1002, "Bu hesap numarası sistemde zaten kayıtlı.", HttpStatus.BAD_REQUEST),

    // Transfer ve Bakiye Hataları (2000'li kodlar) ---
    INSUFFICIENT_BALANCE(2001, "Bakiyeniz bu işlemi gerçekleştirmek için yetersiz.", HttpStatus.BAD_REQUEST),
    INVALID_TRANSACTION_TYPE(2002, "Geçersiz bir işlem tipi gönderildi.", HttpStatus.BAD_REQUEST),

    // Genel Hatalar (9000'li kodlar) ---
    GENERAL_ERROR(9999, "Sunucuda beklenmeyen bir hata oluştu.", HttpStatus.INTERNAL_SERVER_ERROR);

    // Her hatanın bir kodu, mesajı ve HTTP statüsü olmak zorundadır.
    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}
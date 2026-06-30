package com.infina.wallet.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice // Tüm Controller'ları dinleyen ve hata yakalayan mekanizma.
public class GlobalExceptionHandler {

    /**
     * BİZİM KONTROLÜMÜZDEKİ HATALAR:
     * Servis katmanında bilinçli olarak fırlattığımız WalletException hatalarını burada yakalarız.
     */
    @ExceptionHandler(WalletException.class)
    public ResponseEntity<ErrorMessage> handleWalletException(WalletException ex) {
        log.error("Cüzdan Hatası Yakalandı: {}", ex.getMessage());

        // Hatayı şık bir JSON yapısına (ErrorMessage) dönüştürüyoruz.
        ErrorMessage errorMessage = ErrorMessage.builder()
                .code(ex.getErrorType().getCode())
                .message(ex.getMessage())
                .build();

        // Hatayı ve HTTP durum kodunu (400, 404 vb.) dışarıya dönüyoruz.
        return new ResponseEntity<>(errorMessage, ex.getErrorType().getHttpStatus());
    }

    /**
     * BEKLENMEYEN SİSTEM HATALARI:
     * NullPointerException, veritabanı kopması gibi bizim öngöremediğimiz genel hataları yakalar.
     * Kullanıcıya sistemin iç detaylarını (Stack Trace) göstermek yerine genel bir hata döner.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleGlobalException(Exception ex) {
        log.error("Sistem Hatası Yakalandı: {}", ex.getMessage(), ex);

        ErrorMessage errorMessage = ErrorMessage.builder()
                .code(ErrorType.GENERAL_ERROR.getCode())
                .message(ErrorType.GENERAL_ERROR.getMessage())
                .details(List.of(ex.getMessage())) // Hatayı loglamak veya detay görmek için eklendi.
                .build();

        return new ResponseEntity<>(errorMessage, ErrorType.GENERAL_ERROR.getHttpStatus());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleValidationException(org.springframework.web.bind.MethodArgumentNotValidException ex) {
        log.error("DTO Validation Hatası Yakalandı");

        // Spring'in fırlattığı karmaşık hata içinden sadece bizim yazdığımız 'default message' alanlarını süzüyoruz.
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(org.springframework.validation.FieldError::getDefaultMessage)
                .toList();

        ErrorMessage errorMessage = ErrorMessage.builder()
                .code(4000) // Validation hataları için kurumsal bir kod (Örn: 4000)
                .message("Girdiğiniz veriler doğrulanamadı.")
                .details(errors) // Burada sadece ["İşlem tutarı sıfırdan büyük olmalıdır."] yazacak.
                .build();

        return new ResponseEntity<>(errorMessage, org.springframework.http.HttpStatus.BAD_REQUEST);
    }
}
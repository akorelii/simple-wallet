package com.infina.wallet.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice // Bu sınıfın tüm Controller'lardaki hataları dinleyeceğini belirtir
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class) // Kendi fırlattığımız iş kuralları hatalarını yakalar (Örn: Yetersiz bakiye)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex){
        ErrorResponse response = new ErrorResponse(
                ex.getMessage(),
                ex.getErrorCode(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);// 400 Bad Request
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex){
        ErrorResponse response = new ErrorResponse(
                "An unexpected error occurred: " + ex.getMessage(),
                "INTERNAL_SERVER_ERROR",
                LocalDateTime.now()
        );
        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);// 500 Internal Server Error
    }
}

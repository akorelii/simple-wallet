package com.infina.wallet.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorMessage {

    private int code; // ErrorType içindeki 1001, 2004 gibi kodlar buraya gelecek.
    private String message; // Kullanıcı dostu mesaj.
    private List<String> details; // Gerekirse ek hata detayları (validation hataları vb.) için.

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now(); // Hatanın gerçekleştiği anın zaman damgası.
}
package com.infina.wallet.controller;

import com.infina.wallet.dto.TransactionCreateRequest;
import com.infina.wallet.service.ITransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final ITransactionService transactionService;

    /**
     * POST /api/v1/transactions
     * Para yatırma (DEPOSIT), Çekme (WITHDRAW) veya Transfer (TRANSFER) işlemi.
     * Hangi işlemin yapılacağı request gövdesindeki 'transactionType' alanına göre servis katmanında belirlenir.
     */
    @PostMapping
    public ResponseEntity<String> processTransaction(@RequestBody @Valid TransactionCreateRequest request) {
        // İsteği mutfağa (Service) gönderiyoruz.
        transactionService.processTransaction(request);

        // İşlemde herhangi bir WalletException fırlatılmazsa, başarılı kabul edip mesaj dönüyoruz.
        return ResponseEntity.ok("İşlem başarıyla gerçekleştirildi.");
    }
}
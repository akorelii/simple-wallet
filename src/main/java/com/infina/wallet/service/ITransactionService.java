package com.infina.wallet.service;

// Kendi DTO sınıfımızı import ettik.
import com.infina.wallet.dto.TransactionCreateRequest;

public interface ITransactionService {

    /**
     * Dışarıdan (Postman'den vs.) bir işlem isteği geldiğinde tetiklenecek ana metodumuz.
     * Senin DTO'ndaki 'transactionType' (DEPOSIT, WITHDRAW, TRANSFER) ne gelirse gelsin,
     * hepsi bu kapıdan içeri girecek.
     * * @param request Kullanıcının gönderdiği hesap numaraları, miktar ve işlem tipini tutan DTO.
     */
    void processTransaction(TransactionCreateRequest request);

}
package com.infina.wallet.service;

import com.infina.wallet.client.CurrencyClient;
import com.infina.wallet.dto.TransactionCreateRequest;
import com.infina.wallet.entity.Transaction;
import com.infina.wallet.entity.Wallet;
import com.infina.wallet.exception.ErrorType;
import com.infina.wallet.exception.WalletException;
import com.infina.wallet.repository.ITransactionRepository;
import com.infina.wallet.repository.IWalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.infina.wallet.enums.TransactionType.DEPOSIT;
import static com.infina.wallet.enums.TransactionType.WITHDRAW;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements ITransactionService {

    private final IWalletRepository walletRepository;
    private final ITransactionRepository transactionRepository;
    private final CurrencyClient currencyClient;


    @Override
    @Transactional
    public void processTransaction(TransactionCreateRequest request) {
        log.info("İşlem başlatıldı. İşlem Tipi: {}", request.transactionType());

        switch (request.transactionType()) {
            case DEPOSIT -> executeDeposit(request);
            case WITHDRAW -> executeWithdraw(request);
            case TRANSFER -> executeTransfer(request);
            default -> throw new WalletException(ErrorType.INVALID_TRANSACTION_TYPE, "Geçersiz işlem tipi!");
        }
    }

    private void executeDeposit(TransactionCreateRequest request) {
        Wallet wallet = walletRepository.findByAccountNumberForUpdate(request.sourceAccountNumber())
                .orElseThrow(() -> new WalletException(ErrorType.WALLET_NOT_FOUND, "Hesap bulunamadı: " + request.sourceAccountNumber()));

        wallet.setBalance(wallet.getBalance().add(request.amount()));
        walletRepository.save(wallet);

        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .amount(request.amount())
                .transactionType(DEPOSIT)
                .transactionDate(LocalDateTime.now())
                .description("Hesaba para yatırıldı.")
                .build();
        transactionRepository.save(transaction);
    }

    private void executeWithdraw(TransactionCreateRequest request) {
        Wallet wallet = walletRepository.findByAccountNumberForUpdate(request.sourceAccountNumber())
                .orElseThrow(() -> new WalletException(ErrorType.WALLET_NOT_FOUND, "Hesap bulunamadı: " + request.sourceAccountNumber()));

        if (wallet.getBalance().compareTo(request.amount()) < 0) {
            throw new WalletException(ErrorType.INSUFFICIENT_BALANCE, "Yetersiz bakiye! Mevcut: " + wallet.getBalance());
        }

        wallet.setBalance(wallet.getBalance().subtract(request.amount()));
        walletRepository.save(wallet);

        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .amount(request.amount())
                .transactionType(WITHDRAW)
                .transactionDate(LocalDateTime.now())
                .description("Hesaptan para çekildi.")
                .build();
        transactionRepository.save(transaction);
    }

    private void executeTransfer(TransactionCreateRequest request) {

        // Aynı hesaba transfer engellenir.
        if (request.sourceAccountNumber().equals(request.targetAccountNumber())) {
            throw new WalletException(ErrorType.INVALID_TRANSACTION_TYPE, "Aynı hesaba transfer işlemi gerçekleştirilemez.");
        }

        Wallet sourceWallet = walletRepository.findByAccountNumberForUpdate(request.sourceAccountNumber())
                .orElseThrow(() -> new WalletException(ErrorType.WALLET_NOT_FOUND, "Kaynak cüzdan bulunamadı"));

        Wallet targetWallet = walletRepository.findByAccountNumberForUpdate(request.targetAccountNumber())
                .orElseThrow(() -> new WalletException(ErrorType.WALLET_NOT_FOUND, "Hedef cüzdan bulunamadı"));

        // Kaynaktan düşülecek net miktar (Dışarıdan gelen miktarın ta kendisi)
        BigDecimal amountToDeductFromSource = request.amount();

        // Hedefe eklenecek miktar (Aynı para birimiyse doğrudan gelen miktar, farklıysa aşağıda kura çarpılacak)
        BigDecimal amountToAddToTarget = request.amount();

        if (!sourceWallet.getCurrencyType().equals(targetWallet.getCurrencyType())) {
            log.info("Farklı para birimleri tespit edildi. Kur hesaplanıyor...");

            //  Hem kaynak (from) hem hedef (to) birimini gönderiyoruz
            Double rate = currencyClient.getLiveRate(
                    sourceWallet.getCurrencyType().name(),
                    targetWallet.getCurrencyType().name()
            );

            // Gelen miktarı API'den dönen kur ile çarparak hedef hesaba eklenecek döviz miktarını buluyoruz.
            amountToAddToTarget = request.amount().multiply(BigDecimal.valueOf(rate));
        }

        if (sourceWallet.getBalance().compareTo(amountToDeductFromSource) < 0) {
            throw new WalletException(ErrorType.INSUFFICIENT_BALANCE, "Yetersiz bakiye!");
        }

        // Kaynaktan düş, hedefe ekle.
        sourceWallet.setBalance(sourceWallet.getBalance().subtract(amountToDeductFromSource));
        targetWallet.setBalance(targetWallet.getBalance().add(amountToAddToTarget));

        walletRepository.save(sourceWallet);
        walletRepository.save(targetWallet);

        Transaction sourceLog = Transaction.builder()
                .wallet(sourceWallet)
                .amount(amountToDeductFromSource) // Kaynak hesaptan ne kadar TL çıktıysa o loglanır
                .transactionType(WITHDRAW)
                .transactionDate(LocalDateTime.now())
                .description(targetWallet.getAccountNumber() + " hesabına gönderildi.")
                .build();

        Transaction targetLog = Transaction.builder()
                .wallet(targetWallet)
                .amount(amountToAddToTarget) // Hedef hesaba ne kadar USD girdiyse o loglanır
                .transactionType(DEPOSIT)
                .transactionDate(LocalDateTime.now())
                .description(sourceWallet.getAccountNumber() + " hesabından geldi.")
                .build();

        transactionRepository.save(sourceLog);
        transactionRepository.save(targetLog);
    }
}
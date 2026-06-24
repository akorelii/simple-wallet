package com.infina.wallet.service;

import com.infina.wallet.client.CurrencyClient;
import com.infina.wallet.dto.TransactionCreateRequest;
import com.infina.wallet.entity.Transaction;
import com.infina.wallet.entity.Wallet;
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

@Slf4j // Konsola log/bilgi yazdırmak için
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
            default -> throw new RuntimeException("Geçersiz işlem tipi!");
        }
    }

    private void executeDeposit(TransactionCreateRequest request) {
        // PESSIMISTIC LOCK: Başka bir işlem bu hesabı bozmasın diye kilitlendi. (findByAccountNumberForUpdate)
        Wallet wallet = walletRepository.findByAccountNumberForUpdate(request.sourceAccountNumber())
                .orElseThrow(() -> new RuntimeException("Hesap bulunamadı: " + request.sourceAccountNumber()));

        // Mevcut bakiyenin üzerine gelen miktarı ekledik
        wallet.setBalance(wallet.getBalance().add(request.amount()));
        walletRepository.save(wallet); // Güncel cüzdanı veritabanına kaydet.

        // Transaction log oluşturma:
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
        // PESSIMISTIC LOCK
        Wallet wallet = walletRepository.findByAccountNumberForUpdate(request.sourceAccountNumber())
                .orElseThrow(() -> new RuntimeException("Hesap bulunamadı: " + request.sourceAccountNumber()));

        // Bakiye kontrolü (Çekilmek istenen tutar hesaptaki paradan büyükse işlemi durdur)
        if (wallet.getBalance().compareTo(request.amount()) < 0) {
            throw new RuntimeException("Yetersiz bakiye! Mevcut: " + wallet.getBalance());
        }

        // Mevcut bakiyeden miktarı çıkar.
        wallet.setBalance(wallet.getBalance().subtract(request.amount()));
        walletRepository.save(wallet);

        // İşlem geçmişi makbuzunu oluştur.
        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .amount(request.amount())
                .transactionType(WITHDRAW)
                .transactionDate(LocalDateTime.now())
                .description("Hesaptan para çekildi.")
                .build();
        transactionRepository.save(transaction);
    }

    // --- 3. TRANSFER (HESAPLAR ARASI AKTARIM) MANTIĞI ---
    private void executeTransfer(TransactionCreateRequest request) {
        // Her iki hesabı da veritabanından kilitli (Race Condition'a karşı) şekilde buluyoruz.
        Wallet sourceWallet = walletRepository.findByAccountNumberForUpdate(request.sourceAccountNumber())
                .orElseThrow(() -> new RuntimeException("Kaynak cüzdan bulunamadı"));

        Wallet targetWallet = walletRepository.findByAccountNumberForUpdate(request.targetAccountNumber())
                .orElseThrow(() -> new RuntimeException("Hedef cüzdan bulunamadı"));

        // Gönderilecek miktar
        BigDecimal finalAmountInSourceCurrency = request.amount();

        // CANLI KUR HESAPLAMASI (Özkan'ın şirketteki gerçek hayat senaryosu)
        // Eğer hesapların para birimleri aynı değilse (Örn: TRY'den USD'ye atılıyorsa)
        if (!sourceWallet.getCurrencyType().equals(targetWallet.getCurrencyType())) {
            log.info("Farklı para birimleri tespit edildi. Kur hesaplanıyor...");
            Double rate = currencyClient.getLiveRate(targetWallet.getCurrencyType().name());

            // İstediğimiz miktarı API'den dönen kur ile çarparak asıl düşülecek TL miktarını buluyoruz.
            finalAmountInSourceCurrency = request.amount().multiply(BigDecimal.valueOf(rate));
        }

        // Kaynak hesabın parası bu transfer için yeterli mi?
        if (sourceWallet.getBalance().compareTo(finalAmountInSourceCurrency) < 0) {
            throw new RuntimeException("Yetersiz bakiye!");
        }

        // Kaynaktan düş, hedefe ekle.
        sourceWallet.setBalance(sourceWallet.getBalance().subtract(finalAmountInSourceCurrency));
        targetWallet.setBalance(targetWallet.getBalance().add(request.amount()));

        walletRepository.save(sourceWallet);
        walletRepository.save(targetWallet);

        // İşlem makbuzları (Kaynaktan PARA ÇIKTIĞI için ona WITHDRAW, hedefe PARA GİRDİĞİ için ona DEPOSIT kesilir)
        Transaction sourceLog = Transaction.builder()
                .wallet(sourceWallet)
                .amount(finalAmountInSourceCurrency)
                .transactionType(WITHDRAW)
                .transactionDate(LocalDateTime.now())
                .description(targetWallet.getAccountNumber() + " hesabına gönderildi.")
                .build();

        Transaction targetLog = Transaction.builder()
                .wallet(targetWallet)
                .amount(request.amount())
                .transactionType(DEPOSIT)
                .transactionDate(LocalDateTime.now())
                .description(sourceWallet.getAccountNumber() + " hesabından geldi.")
                .build();

        transactionRepository.save(sourceLog);
        transactionRepository.save(targetLog);
    }
}
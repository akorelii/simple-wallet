package com.infina.wallet.controller;

import com.infina.wallet.dto.WalletCreateRequest;
import com.infina.wallet.dto.WalletResponse;
import com.infina.wallet.service.IWalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // Bu sınıfın bir REST API yöneticisi olduğunu belirtir.
@RequestMapping(BaseController.PATH + "/wallets") // Bu sınıfa gelecek tüm isteklerin ana adresi.
@RequiredArgsConstructor
public class WalletController {

    private final IWalletService walletService;

    /**
     * POST /api/v1/wallets
     * Yeni cüzdan oluşturma ucu.
     * @Valid: DTO içindeki @NotBlank, @NotNull gibi kuralları veritabanına inmeden kapıda kontrol eder.
     */
    @PostMapping
    public ResponseEntity<WalletResponse> createWallet(@RequestBody @Valid WalletCreateRequest request) {
        WalletResponse response = walletService.createWallet(request);
        // İşlem başarılıysa HTTP 201 (Created - Oluşturuldu) durum kodu ile cüzdan bilgisini dön.
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * GET /api/v1/wallets/{accountNumber}
     * Hesap numarası ile cüzdan sorgulama ucu.
     */
    @GetMapping("/{accountNumber}")
    public ResponseEntity<WalletResponse> getWallet(@PathVariable String accountNumber) {
        // İşlem başarılıysa HTTP 200 (OK) durum kodu ile cüzdan bilgisini dön.
        return ResponseEntity.ok(walletService.getWalletByAccountNumber(accountNumber));
    }
}
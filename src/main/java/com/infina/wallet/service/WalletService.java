package com.infina.wallet.service;

import com.infina.wallet.dto.WalletCreateRequest;
import com.infina.wallet.dto.WalletResponse;
import com.infina.wallet.entity.Wallet;
import com.infina.wallet.mapper.WalletMapper;
import com.infina.wallet.repository.IWalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final IWalletRepository walletRepository;
    private final WalletMapper walletMapper;

    @Transactional //Spring Framework'ün bize sağladığı ve veritabanı işlemlerinde "Ya hep ya hiç" kuralını işleten bir yapı
    public WalletResponse createWallet(WalletCreateRequest request){

        if (walletRepository.findByAccountNumber(request.accountNumber()).isPresent()) {
            throw new RuntimeException("Wallet already exists with account number: " + request.accountNumber());
        }

        Wallet wallet = walletMapper.toEntity(request);
        wallet.setBalance(BigDecimal.ZERO);

        Wallet savedWallet = walletRepository.save(wallet);
        return walletMapper.toResponse(savedWallet);
    }


    @Transactional(readOnly = true)
    public WalletResponse getWalletByAccountNumber(String accountNumber) {
        Wallet wallet = walletRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        return walletMapper.toResponse(wallet);
    }

}

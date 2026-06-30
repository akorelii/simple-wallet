package com.infina.wallet.service;

import com.infina.wallet.dto.WalletCreateRequest;
import com.infina.wallet.dto.WalletResponse;
import com.infina.wallet.entity.Wallet;
import com.infina.wallet.exception.ErrorType;
import com.infina.wallet.exception.WalletException;
import com.infina.wallet.mapper.WalletMapper;
import com.infina.wallet.repository.IWalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletService implements IWalletService {

    private final IWalletRepository walletRepository;
    private final WalletMapper walletMapper;

    @Transactional //Spring Framework'ün bize sağladığı ve veritabanı işlemlerinde "Ya hep ya hiç" kuralını işleten bir yapı -rollback yb izle
    @Override
    public WalletResponse createWallet(WalletCreateRequest request){

        if (walletRepository.findByAccountNumber(request.accountNumber()).isPresent()) {
            throw new WalletException(ErrorType.WALLET_ALREADY_EXISTS, "Bu hesap numarası zaten kullanımda: "
                    + request.accountNumber());

        }

        Wallet wallet = walletMapper.toEntity(request);
        wallet.setBalance(BigDecimal.ZERO);

        Wallet savedWallet = walletRepository.save(wallet);

        // Veritabanına kaydedilen Entity'i, tekrar dış dünyaya dönecek olan Response DTO'suna çevirdik
        return walletMapper.toResponse(savedWallet);
    }


    @Transactional(readOnly = true)
    public WalletResponse getWalletByAccountNumber(String accountNumber) {
        /*
        Wallet wallet = walletRepository.findByAccountNumber(accountNumber);
        if(wallet == null){
        throw new exceptşon option fark
        }
         */
        Wallet wallet = walletRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new WalletException(ErrorType.WALLET_NOT_FOUND, "Cüzdan bulunamadı: " + accountNumber));
        return walletMapper.toResponse(wallet);
    }

}

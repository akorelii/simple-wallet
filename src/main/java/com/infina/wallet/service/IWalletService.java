package com.infina.wallet.service;

import com.infina.wallet.dto.WalletCreateRequest;
import com.infina.wallet.dto.WalletResponse;


public interface IWalletService {

    //yeni bir cüzdan oluşturmak için kullanılacak kapı
    WalletResponse createWallet(WalletCreateRequest request);

    // hesap numarası verilerek cüzdan bilgilerini getirecek olan kapı
    WalletResponse getWalletByAccountNumber(String accountNumber);}

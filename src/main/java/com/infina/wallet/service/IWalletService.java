package com.infina.wallet.service;

import com.infina.wallet.dto.WalletCreateRequest;
import com.infina.wallet.dto.WalletResponse;


public interface IWalletService {
    WalletResponse createWallet(WalletCreateRequest request);

    WalletResponse getWalletById(long id);
}

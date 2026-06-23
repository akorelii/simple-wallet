package com.infina.wallet.repository;

import com.infina.wallet.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet,Long> {
    // Hesap numarasına göre cüzdan bulmak için kurumsal sorgu metodu findBy
    Optional<Wallet> findByAccountNumber(String accountNumber);
}

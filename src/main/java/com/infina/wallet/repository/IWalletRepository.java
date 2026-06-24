package com.infina.wallet.repository;

import com.infina.wallet.entity.Wallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IWalletRepository extends JpaRepository<Wallet, Long> { //paRepository<Wallet, Long>: Wallet tablosunda işlem yapacağımızı ve Primary Key'in Long tipinde olduğunu söyler.

    // Hesap numarasına göre cüzdanı getiren standart okuma (read) metodu.
    Optional<Wallet> findByAccountNumber(String accountNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.accountNumber = :accountNumber")
    Optional<Wallet> findByAccountNumberForUpdate(@Param("accountNumber") String accountNumber);
}
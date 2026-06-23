package com.infina.wallet.repository;

import com.infina.wallet.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // Temel CRUD (Kayıt, silme vb.) işlemleri için JpaRepository metotları yeterli
}

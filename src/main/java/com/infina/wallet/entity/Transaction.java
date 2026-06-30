package com.infina.wallet.entity;

import com.infina.wallet.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "transactions")
public class Transaction extends BaseEntity { // db-model-controller-service-repository

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @JoinColumn: Veritabanında bu ilişkiyi 'wallet_id' isminde bir kolon ile bağlarız.
    @ManyToOne(fetch = FetchType.LAZY) // LAZY: İşlem geçmişini çekerken cüzdan bilgilerini gereksiz yere veritabanından çekmemek için kullanılır. !!!!!!!!!!!!!!!!!!!!!!!!
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Column(length = 255)
    private String description;
}
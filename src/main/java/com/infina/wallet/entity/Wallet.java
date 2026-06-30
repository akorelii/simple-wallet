package com.infina.wallet.entity;

import com.infina.wallet.enums.CurrencyType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true) // BaseEntity'den gelen tarih alanlarını da karşılaştırmalara (equals/hash) dahil eder.
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "wallets")
public class Wallet extends BaseEntity { // ortak tarih alanlarını BaseEntity'den miras aldık

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID değerinin veritabanı tarafından 1'den başlayarak otomatik artırılacağını belirtir.
    private Long id;

    @Column(name = "account_number", unique = true, nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING) // Veritabanına 0, 1 gibi anlamsız sayılar yerine Enum'ın metin halini (TRY, USD) kaydeder.
    @Column(name = "currency_type", nullable = false)
    private CurrencyType currencyType;
}
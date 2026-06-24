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
public class Wallet extends BaseEntity { // FinFlex standardı gereği ortak tarih alanlarını BaseEntity'den miras alır.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID değerinin veritabanı tarafından 1'den başlayarak otomatik artırılacağını belirtir.
    private Long id;

    @Column(name = "account_number", unique = true, nullable = false) // Hesap numarası benzersiz (unique) ve boş bırakılamaz (nullable = false) olmalıdır.
    private String accountNumber;

    @Column(nullable = false)
    private BigDecimal balance; // Finansal işlemlerde kuruş hassasiyeti için her zaman BigDecimal kullanılır. Double veya Float yuvarlama hatası yapar, asla kullanılmaz.

    @Enumerated(EnumType.STRING) // Veritabanına 0, 1 gibi anlamsız sayılar yerine Enum'ın metin halini (TRY, USD) kaydeder.
    @Column(name = "currency_type", nullable = false)
    private CurrencyType currencyType;
}
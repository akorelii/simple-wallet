package com.infina.wallet.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass // Bu sınıfın tek başına bir veritabanı tablosu olmayacağını,sadece miras alan sınıfların tablolarına bu alanların ekleneceğini belirttik.
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder // Kalıtım (extend) alan sınıflarda 'builder' desenini kullanabilmek için gereklidir.
@EntityListeners(AuditingEntityListener.class) // Tarihleri otomatik atamak için Spring'in dinleyicisini çalıştırır.
public abstract class BaseEntity {

    @CreatedDate // Veritabanına ilk kayıt atıldığı anki tarihi otomatik alır.
    @Column(name = "create_date", updatable = false) // updatable = false: Kayıt bir kere oluştuktan sonra bu tarih kimse tarafından değiştirilemez. Güvenlik için şarttır.
    private LocalDateTime createDate;

    @LastModifiedDate // Kayıt üzerinde herhangi bir güncelleme yapıldığında bu tarihi otomatik günceller.
    @Column(name = "update_date")
    private LocalDateTime updateDate;
}
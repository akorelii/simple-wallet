package com.infina.wallet.mapper;

import com.infina.wallet.dto.WalletCreateRequest;
import com.infina.wallet.dto.WalletResponse;
import com.infina.wallet.entity.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * componentModel = "spring": Bu mapper'ın artık bir Spring bileşeni (Bean) olacağını söyler.
 * Böylece bu sınıfı Service içinde 'private final IWalletMapper walletMapper;' diyerek doğrudan enjekte edebileceğiz.
 * unmappedTargetPolicy = ReportingPolicy.IGNORE: Eşleşmeyen alanlar olursa derleme hatası vermesini engeller.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WalletMapper {

    // Gelen istek DTO'sunu veritabanı Entity nesnesine dönüştürür.
    Wallet toEntity(WalletCreateRequest request);

    // Veritabanından çıkan Entity nesnesini dışarıya döneceğimiz Response DTO'suna dönüştürür.
    WalletResponse toResponse(Wallet wallet);
}
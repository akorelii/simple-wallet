package com.infina.wallet.mapper;

import com.infina.wallet.dto.WalletCreateRequest;
import com.infina.wallet.dto.WalletResponse;
import com.infina.wallet.entity.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring") // Bu mapper'ın Spring bean'i olarak yönetilmesini sağlamak icin
public interface WalletMapper {

    WalletMapper INSTANCE = Mappers.getMapper(WalletMapper.class);

    @Mapping(target = "balance", expression = "java(java.math.BigDecimal.ZERO)")
    @Mapping(target = "transactions", expression = "java(new java.util.ArrayList<>())")
    Wallet toEntity(WalletCreateRequest request);

    WalletResponse toResponse(Wallet wallet);
}

package com.infina.wallet.mapper;

import com.infina.wallet.dto.TransactionCreateRequest;
import com.infina.wallet.dto.TransactionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);


    @Mapping(target = "id", ignore=true)
    @Mapping(target = "transactionDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "wallet", ignore = true)
    Transaction toEntity(TransactionCreateRequest request);// İlişkili cüzdan nesnesi Service katmanında elle set edilecek

    TransactionResponse toResponse(Transaction transaction);
}

package com.creditbank.deal.mapper;

import com.creditbank.deal.entity.Client;
import com.creditbank.deal.entity.Statement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StatementMapper {

    @Mapping(target = "client", source = "client")
    @Mapping(target = "creationDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "statusHistory", expression = "java(new java.util.ArrayList<>())")
    @Mapping(target = "statementId", ignore = true)
    @Mapping(target = "credit", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "appliedOffer", ignore = true)
    @Mapping(target = "signDate", ignore = true)
    @Mapping(target = "sesCode", ignore = true)
    Statement toEntity(Client client);
}


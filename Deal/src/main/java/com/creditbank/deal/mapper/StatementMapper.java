package com.creditbank.deal.mapper;

import com.creditbank.deal.dto.response.StatementDto;
import com.creditbank.deal.entity.Client;
import com.creditbank.deal.entity.Statement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StatementMapper {

    @Mapping(target = "creationDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "statusHistory", expression = "java(new java.util.ArrayList<>())")
    Statement toEntity(Client client);

    StatementDto toDto(Statement statement);
}


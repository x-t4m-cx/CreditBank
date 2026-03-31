package com.creditbank.deal.mapper;

import com.creditbank.deal.dto.FinishRegistrationRequestDto;
import com.creditbank.deal.dto.LoanStatementRequestDto;
import com.creditbank.deal.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = EmploymentMapper.class)
public interface ClientMapper {

    @Mapping(target = "birthDate", source = "birthdate")
    @Mapping(target = "clientId", ignore = true)
    @Mapping(target = "gender", ignore = true)
    @Mapping(target = "maritalStatus", ignore = true)
    @Mapping(target = "dependentAmount", ignore = true)
    @Mapping(target = "passport", ignore = true)
    @Mapping(target = "employment", ignore = true)
    @Mapping(target = "accountNumber", ignore = true)
    @Mapping(target = "statement", ignore = true)
    Client toEntity(LoanStatementRequestDto dto);

    @Mapping(target = "employment", source = "request.employment")
    @Mapping(target = "gender", source = "request.gender")
    @Mapping(target = "maritalStatus", source = "request.maritalStatus")
    @Mapping(target = "dependentAmount", source = "request.dependentAmount")
    @Mapping(target = "accountNumber", source = "request.accountNumber")
    @Mapping(target = "clientId", ignore = true)
    @Mapping(target = "lastName", ignore = true)
    @Mapping(target = "firstName", ignore = true)
    @Mapping(target = "middleName", ignore = true)
    @Mapping(target = "birthDate", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "passport", ignore = true)
    @Mapping(target = "statement", ignore = true)
    void updateEntity(@MappingTarget Client client, FinishRegistrationRequestDto request);
}

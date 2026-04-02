package com.creditbank.deal.mapper;

import com.creditbank.deal.dto.request.FinishRegistrationRequestDto;
import com.creditbank.deal.dto.request.LoanStatementRequestDto;
import com.creditbank.deal.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {EmploymentMapper.class, PassportMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClientMapper {

    @Mapping(target = "birthDate", source = "birthdate")
    @Mapping(target = "passport", source = "dto")
    Client toEntity(LoanStatementRequestDto dto);

    @Mapping(target = "passport.issueBranch", source = "request.passportIssueBranch")
    @Mapping(target = "passport.issueDate", source = "request.passportIssueDate")
    void updateEntity(@MappingTarget Client client, FinishRegistrationRequestDto request);
}

package com.creditbank.deal.mapper;

import com.creditbank.deal.dto.FinishRegistrationRequestDto;
import com.creditbank.deal.dto.LoanStatementRequestDto;
import com.creditbank.deal.jsonb.Passport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PassportMapper {

    @Mapping(target = "series", source = "passportSeries")
    @Mapping(target = "number", source = "passportNumber")
    @Mapping(target = "issueBranch", ignore = true)
    @Mapping(target = "issueDate", ignore = true)
    Passport toModel(LoanStatementRequestDto dto);

    @Mapping(target = "issueBranch", source = "passportIssueBranch")
    @Mapping(target = "issueDate", source = "passportIssueDate")
    @Mapping(target = "series", ignore = true)
    @Mapping(target = "number", ignore = true)
    void updateModel(@MappingTarget Passport passport, FinishRegistrationRequestDto request);
}

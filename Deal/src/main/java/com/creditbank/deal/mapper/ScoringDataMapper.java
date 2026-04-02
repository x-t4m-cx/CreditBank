package com.creditbank.deal.mapper;

import com.creditbank.deal.dto.calculator.request.ScoringDataDto;
import com.creditbank.deal.entity.Statement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = EmploymentMapper.class)
public interface ScoringDataMapper {

    @Mapping(target = "amount", source = "statement.appliedOffer.totalAmount")
    @Mapping(target = "term", source = "statement.appliedOffer.term")
    @Mapping(target = "firstName", source = "statement.client.firstName")
    @Mapping(target = "lastName", source = "statement.client.lastName")
    @Mapping(target = "middleName", source = "statement.client.middleName")
    @Mapping(target = "gender", source = "statement.client.gender")
    @Mapping(target = "birthdate", source = "statement.client.birthDate")
    @Mapping(target = "passportSeries", source = "statement.client.passport.series")
    @Mapping(target = "passportNumber", source = "statement.client.passport.number")
    @Mapping(target = "passportIssueDate", source = "statement.client.passport.issueDate")
    @Mapping(target = "passportIssueBranch", source = "statement.client.passport.issueBranch")
    @Mapping(target = "maritalStatus", source = "statement.client.maritalStatus")
    @Mapping(target = "dependentAmount", source = "statement.client.dependentAmount")
    @Mapping(target = "employment", source = "statement.client.employment")
    @Mapping(target = "accountNumber", source = "statement.client.accountNumber")
    @Mapping(target = "isInsuranceEnabled", source = "statement.appliedOffer.insuranceEnabled")
    @Mapping(target = "isSalaryClient", source = "statement.appliedOffer.salaryClient")
    ScoringDataDto toDto(Statement statement);
}


package com.creditbank.deal.mapper;

import com.creditbank.deal.dto.EmploymentDto;
import com.creditbank.deal.dto.ScoringDataDto;
import com.creditbank.deal.entity.Client;
import com.creditbank.deal.model.LoanOffer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ScoringDataMapper {

    @Mapping(target = "amount", source = "offer.totalAmount")
    @Mapping(target = "term", source = "offer.term")
    @Mapping(target = "firstName", source = "client.firstName")
    @Mapping(target = "lastName", source = "client.lastName")
    @Mapping(target = "middleName", source = "client.middleName")
    @Mapping(target = "gender", source = "client.gender")
    @Mapping(target = "birthdate", source = "client.birthDate")
    @Mapping(target = "passportSeries", source = "client.passport.series")
    @Mapping(target = "passportNumber", source = "client.passport.number")
    @Mapping(target = "passportIssueDate", source = "client.passport.issueDate")
    @Mapping(target = "passportIssueBranch", source = "client.passport.issueBranch")
    @Mapping(target = "maritalStatus", source = "client.maritalStatus")
    @Mapping(target = "dependentAmount", source = "client.dependentAmount")
    @Mapping(target = "employment", source = "employment")
    @Mapping(target = "accountNumber", source = "client.accountNumber")
    @Mapping(target = "isInsuranceEnabled", source = "offer.insuranceEnabled")
    @Mapping(target = "isSalaryClient", source = "offer.salaryClient")
    ScoringDataDto toDto(Client client, LoanOffer offer, EmploymentDto employment);
}


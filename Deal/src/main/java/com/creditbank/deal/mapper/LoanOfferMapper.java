package com.creditbank.deal.mapper;

import com.creditbank.deal.dto.response.LoanOfferDto;
import com.creditbank.deal.model.LoanOffer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoanOfferMapper {

    @Mapping(target = "insuranceEnabled", source = "isInsuranceEnabled")
    @Mapping(target = "salaryClient", source = "isSalaryClient")
    LoanOffer toModel(LoanOfferDto dto);
}
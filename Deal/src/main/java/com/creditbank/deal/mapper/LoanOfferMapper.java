package com.creditbank.deal.mapper;

import com.creditbank.deal.dto.LoanOfferDto;
import com.creditbank.deal.model.LoanOffer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LoanOfferMapper {

    @Mapping(target = "insuranceEnabled", source = "isInsuranceEnabled")
    @Mapping(target = "salaryClient", source = "isSalaryClient")
    LoanOffer toModel(LoanOfferDto dto);

    @Mapping(target = "isInsuranceEnabled", source = "insuranceEnabled")
    @Mapping(target = "isSalaryClient", source = "salaryClient")
    LoanOfferDto toDto(LoanOffer model);

    List<LoanOffer> toModelList(List<LoanOfferDto> dtoList);

    List<LoanOfferDto> toDtoList(List<LoanOffer> modelList);
}
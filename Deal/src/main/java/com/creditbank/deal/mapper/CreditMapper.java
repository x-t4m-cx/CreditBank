package com.creditbank.deal.mapper;

import com.creditbank.deal.dto.calculator.response.CreditDto;
import com.creditbank.deal.entity.Credit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CreditMapper {

    @Mapping(source = "isInsuranceEnabled", target = "insuranceEnabled")
    @Mapping(source = "isSalaryClient", target = "salaryClient")
    @Mapping(target = "creditId", ignore = true)
    @Mapping(target = "creditStatus", ignore = true)
    @Mapping(target = "statement", ignore = true)
    Credit toEntity(CreditDto dto);
}

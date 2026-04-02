package com.creditbank.deal.mapper;

import com.creditbank.deal.dto.request.LoanStatementRequestDto;
import com.creditbank.deal.entity.jsonb.Passport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PassportMapper {

    @Mapping(target = "series", source = "passportSeries")
    @Mapping(target = "number", source = "passportNumber")
    Passport toModel(LoanStatementRequestDto dto);
}

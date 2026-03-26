package com.creditbank.deal.mapper;

import com.creditbank.deal.dto.response.LoanOfferDto;
import com.creditbank.deal.model.LoanOffer;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LoanOfferMapper {

    LoanOffer toModel(LoanOfferDto dto);

    LoanOfferDto toDto(LoanOffer model);

    List<LoanOffer> toModelList(List<LoanOfferDto> dtoList);

    List<LoanOfferDto> toDtoList(List<LoanOffer> modelList);
}
package com.creditbank.deal.mapper;

import com.creditbank.deal.dto.PaymentScheduleElementDto;
import com.creditbank.deal.model.PaymentScheduleElement;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentScheduleMapper {

    PaymentScheduleElement toModel(PaymentScheduleElementDto dto);

    PaymentScheduleElementDto toDto(PaymentScheduleElement model);

    List<PaymentScheduleElement> toModelList(List<PaymentScheduleElementDto> dtos);

    List<PaymentScheduleElementDto> toDtoList(List<PaymentScheduleElement> models);
}

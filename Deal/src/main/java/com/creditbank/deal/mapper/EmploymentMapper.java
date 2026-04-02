package com.creditbank.deal.mapper;

import com.creditbank.deal.dto.request.EmploymentDto;
import com.creditbank.deal.entity.jsonb.Employment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmploymentMapper {

    @Mapping(target = "status", source = "employmentStatus")
    @Mapping(target = "employmentInn", source = "employerINN")
    Employment toModel(EmploymentDto dto);
}

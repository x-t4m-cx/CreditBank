package com.creditbank.deal.mapper;

import com.creditbank.deal.dto.EmploymentDto;
import com.creditbank.deal.jsonb.Employment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmploymentMapper {

    @Mapping(target = "status", source = "employmentStatus")
    @Mapping(target = "employmentInn", source = "employerINN")
    Employment toModel(EmploymentDto dto);

    @Mapping(target = "employmentStatus", source = "status")
    @Mapping(target = "employerINN", source = "employmentInn")
    EmploymentDto toDto(Employment model);
}

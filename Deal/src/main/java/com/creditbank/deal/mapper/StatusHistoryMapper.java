package com.creditbank.deal.mapper;

import com.creditbank.deal.enums.ApplicationStatus;
import com.creditbank.deal.enums.ChangeType;
import com.creditbank.deal.jsonb.StatusHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StatusHistoryMapper {

    @Mapping(target = "status", expression = "java(status.name())")
    @Mapping(target = "time", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "changeType", source = "changeType")
    StatusHistory toEntity(ApplicationStatus status, ChangeType changeType);
}


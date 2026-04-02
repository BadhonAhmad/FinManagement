package com.finmanagement.mapper;

import com.finmanagement.dto.record.RecordRequest;
import com.finmanagement.dto.record.RecordResponse;
import com.finmanagement.entity.FinancialRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FinancialRecordMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    FinancialRecord toEntity(RecordRequest request);

    @Mapping(target = "createdAt", source = "createdAt")
    RecordResponse toResponse(FinancialRecord record);

    List<RecordResponse> toResponseList(List<FinancialRecord> records);
}

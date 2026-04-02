package com.finmanagement.service;

import com.finmanagement.dto.common.PagedResponse;
import com.finmanagement.dto.record.RecordRequest;
import com.finmanagement.dto.record.RecordResponse;
import com.finmanagement.entity.enums.RecordType;

import java.time.LocalDate;

public interface FinancialRecordService {
    RecordResponse createRecord(Long userId, RecordRequest request);
    PagedResponse<RecordResponse> getRecords(Long userId, RecordType type, String category,
                                              LocalDate startDate, LocalDate endDate, String search,
                                              int page, int size);
    RecordResponse getRecordById(Long userId, Long recordId, boolean isAdmin);
    RecordResponse updateRecord(Long userId, Long recordId, RecordRequest request, boolean isAdmin);
    void deleteRecord(Long userId, Long recordId, boolean isAdmin);
}

package com.finmanagement.service.impl;

import com.finmanagement.dto.record.RecordRequest;
import com.finmanagement.dto.record.RecordResponse;
import com.finmanagement.entity.FinancialRecord;
import com.finmanagement.entity.User;
import com.finmanagement.entity.enums.RecordType;
import com.finmanagement.entity.enums.Role;
import com.finmanagement.exception.ResourceNotFoundException;
import com.finmanagement.exception.UnauthorizedException;
import com.finmanagement.mapper.FinancialRecordMapper;
import com.finmanagement.repository.FinancialRecordRepository;
import com.finmanagement.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinancialRecordServiceImplTest {

    @Mock private FinancialRecordRepository recordRepository;
    @Mock private UserRepository userRepository;
    @Mock private FinancialRecordMapper recordMapper;

    @InjectMocks private FinancialRecordServiceImpl recordService;

    @Test
    void createRecord_success() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        RecordRequest request = new RecordRequest();
        request.setAmount(BigDecimal.valueOf(100));
        request.setType(RecordType.INCOME);
        request.setCategory("SALARY");
        request.setDate(LocalDate.now());

        FinancialRecord record = new FinancialRecord();
        record.setId(1L);
        when(recordMapper.toEntity(request)).thenReturn(record);
        when(recordRepository.save(any(FinancialRecord.class))).thenReturn(record);

        RecordResponse response = new RecordResponse();
        response.setId(1L);
        response.setAmount(BigDecimal.valueOf(100));
        when(recordMapper.toResponse(any(FinancialRecord.class))).thenReturn(response);

        RecordResponse result = recordService.createRecord(1L, request);
        assertEquals(BigDecimal.valueOf(100), result.getAmount());
        verify(recordRepository).save(any(FinancialRecord.class));
    }

    @Test
    void getRecordById_ownerAccess() {
        User user = new User();
        user.setId(1L);
        FinancialRecord record = new FinancialRecord();
        record.setId(1L);
        record.setUser(user);

        when(recordRepository.findById(1L)).thenReturn(Optional.of(record));
        when(recordMapper.toResponse(record)).thenReturn(new RecordResponse());

        RecordResponse result = recordService.getRecordById(1L, 1L, false);
        assertNotNull(result);
    }

    @Test
    void getRecordById_nonOwnerNonAdmin_throwsException() {
        User user = new User();
        user.setId(1L);
        FinancialRecord record = new FinancialRecord();
        record.setId(1L);
        record.setUser(user);

        when(recordRepository.findById(1L)).thenReturn(Optional.of(record));

        assertThrows(UnauthorizedException.class, () -> recordService.getRecordById(2L, 1L, false));
    }

    @Test
    void getRecordById_adminAccess() {
        User user = new User();
        user.setId(1L);
        FinancialRecord record = new FinancialRecord();
        record.setId(1L);
        record.setUser(user);

        when(recordRepository.findById(1L)).thenReturn(Optional.of(record));
        when(recordMapper.toResponse(record)).thenReturn(new RecordResponse());

        RecordResponse result = recordService.getRecordById(2L, 1L, true);
        assertNotNull(result);
    }

    @Test
    void deleteRecord_softDelete() {
        User user = new User();
        user.setId(1L);
        FinancialRecord record = new FinancialRecord();
        record.setId(1L);
        record.setUser(user);

        when(recordRepository.findById(1L)).thenReturn(Optional.of(record));

        recordService.deleteRecord(1L, 1L, false);

        verify(recordRepository).save(argThat(r -> r.isDeleted()));
    }

    @Test
    void getRecordById_notFound_throwsException() {
        when(recordRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> recordService.getRecordById(1L, 999L, false));
    }
}

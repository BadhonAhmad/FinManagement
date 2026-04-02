package com.finmanagement.service.impl;

import com.finmanagement.dto.dashboard.DashboardSummaryResponse;
import com.finmanagement.mapper.FinancialRecordMapper;
import com.finmanagement.repository.FinancialRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock private FinancialRecordRepository recordRepository;
    @Mock private FinancialRecordMapper recordMapper;

    @InjectMocks private DashboardServiceImpl dashboardService;

    @Test
    void getSummary_withDateRange() {
        LocalDate start = LocalDate.of(2026, 4, 1);
        LocalDate end = LocalDate.of(2026, 4, 30);

        when(recordRepository.sumByUserIdAndTypeAndDateBetween(1L, com.finmanagement.entity.enums.RecordType.INCOME, start, end))
                .thenReturn(BigDecimal.valueOf(5000));
        when(recordRepository.sumByUserIdAndTypeAndDateBetween(1L, com.finmanagement.entity.enums.RecordType.EXPENSE, start, end))
                .thenReturn(BigDecimal.valueOf(3000));
        when(recordRepository.countByUserIdAndDateBetween(1L, start, end))
                .thenReturn(10L);

        DashboardSummaryResponse result = dashboardService.getSummary(1L, start, end);

        assertEquals(BigDecimal.valueOf(5000), result.getTotalIncome());
        assertEquals(BigDecimal.valueOf(3000), result.getTotalExpenses());
        assertEquals(BigDecimal.valueOf(2000), result.getNetBalance());
        assertEquals(10L, result.getRecordCount());
    }

    @Test
    void getSummary_nullDates_defaultsToCurrentMonth() {
        when(recordRepository.sumByUserIdAndTypeAndDateBetween(anyLong(), any(), any(), any()))
                .thenReturn(BigDecimal.ZERO);
        when(recordRepository.countByUserIdAndDateBetween(anyLong(), any(), any()))
                .thenReturn(0L);

        DashboardSummaryResponse result = dashboardService.getSummary(1L, null, null);

        assertNotNull(result);
        assertNotNull(result.getPeriodStart());
        assertNotNull(result.getPeriodEnd());
    }

    @Test
    void getAdminSummary() {
        LocalDate start = LocalDate.of(2026, 4, 1);
        LocalDate end = LocalDate.of(2026, 4, 30);

        when(recordRepository.sumByTypeAndDateBetween(com.finmanagement.entity.enums.RecordType.INCOME, start, end))
                .thenReturn(BigDecimal.valueOf(50000));
        when(recordRepository.sumByTypeAndDateBetween(com.finmanagement.entity.enums.RecordType.EXPENSE, start, end))
                .thenReturn(BigDecimal.valueOf(30000));
        when(recordRepository.countByDateBetween(start, end))
                .thenReturn(100L);

        DashboardSummaryResponse result = dashboardService.getAdminSummary(start, end);

        assertEquals(BigDecimal.valueOf(50000), result.getTotalIncome());
        assertEquals(BigDecimal.valueOf(30000), result.getTotalExpenses());
        assertEquals(BigDecimal.valueOf(20000), result.getNetBalance());
    }
}

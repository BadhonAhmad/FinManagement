package com.finmanagement.service.impl;

import com.finmanagement.dto.dashboard.CategoryTotalResponse;
import com.finmanagement.dto.dashboard.DashboardSummaryResponse;
import com.finmanagement.dto.dashboard.TrendDataResponse;
import com.finmanagement.dto.record.RecordResponse;
import com.finmanagement.entity.enums.RecordType;
import com.finmanagement.mapper.FinancialRecordMapper;
import com.finmanagement.repository.FinancialRecordRepository;
import com.finmanagement.service.DashboardService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final FinancialRecordRepository recordRepository;
    private final FinancialRecordMapper recordMapper;

    public DashboardServiceImpl(FinancialRecordRepository recordRepository, FinancialRecordMapper recordMapper) {
        this.recordRepository = recordRepository;
        this.recordMapper = recordMapper;
    }

    @Override
    public DashboardSummaryResponse getSummary(Long userId, LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            YearMonth currentMonth = YearMonth.now();
            startDate = currentMonth.atDay(1);
            endDate = currentMonth.atEndOfMonth();
        }

        BigDecimal totalIncome = recordRepository.sumByUserIdAndTypeAndDateBetween(userId, RecordType.INCOME, startDate, endDate);
        BigDecimal totalExpenses = recordRepository.sumByUserIdAndTypeAndDateBetween(userId, RecordType.EXPENSE, startDate, endDate);
        long recordCount = recordRepository.countByUserIdAndDateBetween(userId, startDate, endDate);

        DashboardSummaryResponse response = new DashboardSummaryResponse();
        response.setTotalIncome(totalIncome);
        response.setTotalExpenses(totalExpenses);
        response.setNetBalance(totalIncome.subtract(totalExpenses));
        response.setRecordCount(recordCount);
        response.setPeriodStart(startDate.toString());
        response.setPeriodEnd(endDate.toString());

        return response;
    }

    @Override
    public List<CategoryTotalResponse> getCategoryTotals(Long userId, LocalDate startDate, LocalDate endDate, String type) {
        if (startDate == null || endDate == null) {
            YearMonth currentMonth = YearMonth.now();
            startDate = currentMonth.atDay(1);
            endDate = currentMonth.atEndOfMonth();
        }

        List<Object[]> results = recordRepository.getCategoryTotals(userId, startDate, endDate);

        return results.stream()
                .filter(row -> type == null || row[1].toString().equals(type))
                .map(row -> new CategoryTotalResponse(
                        (String) row[0],
                        RecordType.valueOf(row[1].toString()),
                        toBigDecimal(row[2]),
                        ((Number) row[3]).longValue()
                ))
                .toList();
    }

    @Override
    public List<RecordResponse> getRecentActivity(Long userId, int limit) {
        limit = Math.min(limit, 50);
        return recordRepository.findByUserIdOrderByDateDesc(userId, PageRequest.of(0, limit))
                .stream()
                .map(recordMapper::toResponse)
                .toList();
    }

    @Override
    public List<TrendDataResponse> getTrends(Long userId, String period, int count) {
        LocalDate startDate;
        boolean weekly = "WEEKLY".equalsIgnoreCase(period);

        if (weekly) {
            count = Math.min(count, 52);
            startDate = LocalDate.now().minusWeeks(count);
        } else {
            count = Math.min(count, 24);
            startDate = LocalDate.now().minusMonths(count);
        }

        List<Object[]> results = recordRepository.getUserRecordsForTrends(userId, startDate);

        // Group by period key in Java
        Map<String, BigDecimal> incomeByPeriod = new LinkedHashMap<>();
        Map<String, BigDecimal> expenseByPeriod = new LinkedHashMap<>();

        for (Object[] row : results) {
            LocalDate date = (LocalDate) row[0];
            RecordType type = (RecordType) row[1];
            BigDecimal amount = (BigDecimal) row[2];

            String key = weekly
                    ? date.getYear() + "-W" + String.format("%02d", date.getDayOfYear() / 7 + 1)
                    : date.getYear() + "-" + String.format("%02d", date.getMonthValue());

            if (type == RecordType.INCOME) {
                incomeByPeriod.merge(key, amount, BigDecimal::add);
            } else {
                expenseByPeriod.merge(key, amount, BigDecimal::add);
            }
        }

        Set<String> allPeriods = new TreeSet<>();
        allPeriods.addAll(incomeByPeriod.keySet());
        allPeriods.addAll(expenseByPeriod.keySet());

        return allPeriods.stream()
                .map(p -> {
                    BigDecimal income = incomeByPeriod.getOrDefault(p, BigDecimal.ZERO);
                    BigDecimal expenses = expenseByPeriod.getOrDefault(p, BigDecimal.ZERO);
                    return new TrendDataResponse(p, income, expenses, income.subtract(expenses));
                })
                .toList();
    }

    @Override
    public DashboardSummaryResponse getAdminSummary(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            YearMonth currentMonth = YearMonth.now();
            startDate = currentMonth.atDay(1);
            endDate = currentMonth.atEndOfMonth();
        }

        BigDecimal totalIncome = recordRepository.sumByTypeAndDateBetween(RecordType.INCOME, startDate, endDate);
        BigDecimal totalExpenses = recordRepository.sumByTypeAndDateBetween(RecordType.EXPENSE, startDate, endDate);
        long recordCount = recordRepository.countByDateBetween(startDate, endDate);

        DashboardSummaryResponse response = new DashboardSummaryResponse();
        response.setTotalIncome(totalIncome);
        response.setTotalExpenses(totalExpenses);
        response.setNetBalance(totalIncome.subtract(totalExpenses));
        response.setRecordCount(recordCount);
        response.setPeriodStart(startDate.toString());
        response.setPeriodEnd(endDate.toString());

        return response;
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value instanceof BigDecimal bd) return bd;
        if (value instanceof Number num) return BigDecimal.valueOf(num.doubleValue());
        return BigDecimal.ZERO;
    }
}

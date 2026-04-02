package com.finmanagement.service;

import com.finmanagement.dto.dashboard.CategoryTotalResponse;
import com.finmanagement.dto.dashboard.DashboardSummaryResponse;
import com.finmanagement.dto.dashboard.TrendDataResponse;
import com.finmanagement.dto.record.RecordResponse;

import java.time.LocalDate;
import java.util.List;

public interface DashboardService {
    DashboardSummaryResponse getSummary(Long userId, LocalDate startDate, LocalDate endDate);
    List<CategoryTotalResponse> getCategoryTotals(Long userId, LocalDate startDate, LocalDate endDate, String type);
    List<RecordResponse> getRecentActivity(Long userId, int limit);
    List<TrendDataResponse> getTrends(Long userId, String period, int count);
    DashboardSummaryResponse getAdminSummary(LocalDate startDate, LocalDate endDate);
}

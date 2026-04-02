package com.finmanagement.controller;

import com.finmanagement.dto.common.ApiResponse;
import com.finmanagement.dto.dashboard.CategoryTotalResponse;
import com.finmanagement.dto.dashboard.DashboardSummaryResponse;
import com.finmanagement.dto.dashboard.TrendDataResponse;
import com.finmanagement.dto.record.RecordResponse;
import com.finmanagement.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Dashboard and analytics endpoints")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    @Operation(summary = "Get financial summary")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getSummary(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            Authentication authentication) {
        Long userId = getUserId(authentication);
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getSummary(userId, startDate, endDate)));
    }

    @GetMapping("/category-totals")
    @Operation(summary = "Get category-wise totals")
    public ResponseEntity<ApiResponse<List<CategoryTotalResponse>>> getCategoryTotals(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String type,
            Authentication authentication) {
        Long userId = getUserId(authentication);
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getCategoryTotals(userId, startDate, endDate, type)));
    }

    @GetMapping("/recent-activity")
    @Operation(summary = "Get recent activity")
    public ResponseEntity<ApiResponse<List<RecordResponse>>> getRecentActivity(
            @RequestParam(defaultValue = "10") int limit,
            Authentication authentication) {
        Long userId = getUserId(authentication);
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getRecentActivity(userId, limit)));
    }

    @GetMapping("/trends")
    @Operation(summary = "Get income/expense trends")
    public ResponseEntity<ApiResponse<List<TrendDataResponse>>> getTrends(
            @RequestParam(defaultValue = "MONTHLY") String period,
            @RequestParam(defaultValue = "6") int count,
            Authentication authentication) {
        Long userId = getUserId(authentication);
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getTrends(userId, period, count)));
    }

    @GetMapping("/admin/summary")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get admin summary (all users)")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getAdminSummary(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getAdminSummary(startDate, endDate)));
    }

    private Long getUserId(Authentication authentication) {
        return (Long) authentication.getDetails();
    }
}

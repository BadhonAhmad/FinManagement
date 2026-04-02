package com.finmanagement.controller;

import com.finmanagement.dto.common.ApiResponse;
import com.finmanagement.dto.common.PagedResponse;
import com.finmanagement.dto.record.RecordRequest;
import com.finmanagement.dto.record.RecordResponse;
import com.finmanagement.entity.enums.RecordType;
import com.finmanagement.service.FinancialRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/records")
@Tag(name = "Financial Records", description = "Financial record CRUD endpoints")
public class FinancialRecordController {

    private final FinancialRecordService recordService;

    public FinancialRecordController(FinancialRecordService recordService) {
        this.recordService = recordService;
    }

    @PostMapping
    @Operation(summary = "Create a new financial record")
    public ResponseEntity<ApiResponse<RecordResponse>> createRecord(
            @Valid @RequestBody RecordRequest request, Authentication authentication) {
        Long userId = getUserId(authentication);
        RecordResponse response = recordService.createRecord(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Record created successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all records (paginated, filterable)")
    public ResponseEntity<ApiResponse<PagedResponse<RecordResponse>>> getRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) RecordType type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String search,
            Authentication authentication) {
        Long userId = getUserId(authentication);
        PagedResponse<RecordResponse> response = recordService.getRecords(userId, type, category, startDate, endDate, search, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get record by ID")
    public ResponseEntity<ApiResponse<RecordResponse>> getRecordById(@PathVariable Long id, Authentication authentication) {
        Long userId = getUserId(authentication);
        boolean isAdmin = isAdmin(authentication);
        RecordResponse response = recordService.getRecordById(userId, id, isAdmin);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update record")
    public ResponseEntity<ApiResponse<RecordResponse>> updateRecord(
            @PathVariable Long id, @Valid @RequestBody RecordRequest request, Authentication authentication) {
        Long userId = getUserId(authentication);
        boolean isAdmin = isAdmin(authentication);
        RecordResponse response = recordService.updateRecord(userId, id, request, isAdmin);
        return ResponseEntity.ok(ApiResponse.success("Record updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete record (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deleteRecord(@PathVariable Long id, Authentication authentication) {
        Long userId = getUserId(authentication);
        boolean isAdmin = isAdmin(authentication);
        recordService.deleteRecord(userId, id, isAdmin);
        return ResponseEntity.ok(ApiResponse.success("Record deleted successfully"));
    }

    private Long getUserId(Authentication authentication) {
        return (Long) authentication.getDetails();
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
}

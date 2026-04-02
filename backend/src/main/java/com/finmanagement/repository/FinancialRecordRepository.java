package com.finmanagement.repository;

import com.finmanagement.entity.FinancialRecord;
import com.finmanagement.entity.enums.RecordType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {

    Page<FinancialRecord> findAll(Specification<FinancialRecord> spec, Pageable pageable);

    @Query("SELECT r FROM FinancialRecord r WHERE r.user.id = :userId AND r.id = :recordId AND r.deleted = false")
    Optional<FinancialRecord> findByUserIdAndRecordId(@Param("userId") Long userId, @Param("recordId") Long recordId);

    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM FinancialRecord r " +
           "WHERE r.user.id = :userId AND r.type = :type AND r.date BETWEEN :startDate AND :endDate AND r.deleted = false")
    BigDecimal sumByUserIdAndTypeAndDateBetween(@Param("userId") Long userId,
                                                 @Param("type") RecordType type,
                                                 @Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(r) FROM FinancialRecord r " +
           "WHERE r.user.id = :userId AND r.date BETWEEN :startDate AND :endDate AND r.deleted = false")
    long countByUserIdAndDateBetween(@Param("userId") Long userId,
                                      @Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);

    @Query("SELECT r.category, r.type, COALESCE(SUM(r.amount), 0), COUNT(r) FROM FinancialRecord r " +
           "WHERE r.user.id = :userId AND r.date BETWEEN :startDate AND :endDate AND r.deleted = false " +
           "GROUP BY r.category, r.type ORDER BY SUM(r.amount) DESC")
    List<Object[]> getCategoryTotals(@Param("userId") Long userId,
                                      @Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);

    @Query(value = "SELECT strftime('%Y-%m', date) as period, " +
           "COALESCE(SUM(CASE WHEN type = 'INCOME' THEN amount ELSE 0 END), 0) as total_income, " +
           "COALESCE(SUM(CASE WHEN type = 'EXPENSE' THEN amount ELSE 0 END), 0) as total_expenses " +
           "FROM financial_records " +
           "WHERE user_id = :userId AND deleted = false AND date >= :startDate " +
           "GROUP BY strftime('%Y-%m', date) ORDER BY period",
           nativeQuery = true)
    List<Object[]> getMonthlyTrends(@Param("userId") Long userId, @Param("startDate") LocalDate startDate);

    @Query(value = "SELECT strftime('%Y-%W', date) as period, " +
           "COALESCE(SUM(CASE WHEN type = 'INCOME' THEN amount ELSE 0 END), 0) as total_income, " +
           "COALESCE(SUM(CASE WHEN type = 'EXPENSE' THEN amount ELSE 0 END), 0) as total_expenses " +
           "FROM financial_records " +
           "WHERE user_id = :userId AND deleted = false AND date >= :startDate " +
           "GROUP BY strftime('%Y-%W', date) ORDER BY period",
           nativeQuery = true)
    List<Object[]> getWeeklyTrends(@Param("userId") Long userId, @Param("startDate") LocalDate startDate);

    // Admin dashboard queries - aggregate across all users
    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM FinancialRecord r " +
           "WHERE r.type = :type AND r.date BETWEEN :startDate AND :endDate AND r.deleted = false")
    BigDecimal sumByTypeAndDateBetween(@Param("type") RecordType type,
                                        @Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(r) FROM FinancialRecord r " +
           "WHERE r.date BETWEEN :startDate AND :endDate AND r.deleted = false")
    long countByDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    List<FinancialRecord> findByUserIdOrderByDateDesc(Long userId, Pageable pageable);
}

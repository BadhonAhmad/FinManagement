package com.finmanagement.service.impl;

import com.finmanagement.dto.common.PagedResponse;
import com.finmanagement.dto.record.RecordRequest;
import com.finmanagement.dto.record.RecordResponse;
import com.finmanagement.entity.FinancialRecord;
import com.finmanagement.entity.User;
import com.finmanagement.entity.enums.RecordType;
import com.finmanagement.exception.ResourceNotFoundException;
import com.finmanagement.exception.UnauthorizedException;
import com.finmanagement.mapper.FinancialRecordMapper;
import com.finmanagement.repository.FinancialRecordRepository;
import com.finmanagement.repository.UserRepository;
import com.finmanagement.service.FinancialRecordService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class FinancialRecordServiceImpl implements FinancialRecordService {

    private final FinancialRecordRepository recordRepository;
    private final UserRepository userRepository;
    private final FinancialRecordMapper recordMapper;

    public FinancialRecordServiceImpl(FinancialRecordRepository recordRepository,
                                       UserRepository userRepository,
                                       FinancialRecordMapper recordMapper) {
        this.recordRepository = recordRepository;
        this.userRepository = userRepository;
        this.recordMapper = recordMapper;
    }

    @Override
    @Transactional
    public RecordResponse createRecord(Long userId, RecordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        FinancialRecord record = recordMapper.toEntity(request);
        record.setUser(user);
        record = recordRepository.save(record);

        return recordMapper.toResponse(record);
    }

    @Override
    public PagedResponse<RecordResponse> getRecords(Long userId, RecordType type, String category,
                                                      LocalDate startDate, LocalDate endDate, String search,
                                                      int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending().and(Sort.by("createdAt").descending()));

        Specification<FinancialRecord> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("user").get("id"), userId));
            predicates.add(cb.isFalse(root.get("deleted")));

            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
            }
            if (StringUtils.hasText(category)) {
                predicates.add(cb.equal(root.get("category"), category));
            }
            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), startDate));
            }
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), endDate));
            }
            if (StringUtils.hasText(search)) {
                predicates.add(cb.like(cb.lower(root.get("notes")), "%" + search.toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<FinancialRecord> recordPage = recordRepository.findAll(spec, pageable);
        return new PagedResponse<>(
                recordMapper.toResponseList(recordPage.getContent()),
                recordPage.getNumber(),
                recordPage.getSize(),
                recordPage.getTotalElements(),
                recordPage.getTotalPages(),
                recordPage.isLast()
        );
    }

    @Override
    public RecordResponse getRecordById(Long userId, Long recordId, boolean isAdmin) {
        FinancialRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Record", recordId));

        if (!isAdmin && !record.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You do not have permission to access this record");
        }

        return recordMapper.toResponse(record);
    }

    @Override
    @Transactional
    public RecordResponse updateRecord(Long userId, Long recordId, RecordRequest request, boolean isAdmin) {
        FinancialRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Record", recordId));

        if (!isAdmin && !record.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You do not have permission to modify this record");
        }

        if (request.getAmount() != null) record.setAmount(request.getAmount());
        if (request.getType() != null) record.setType(request.getType());
        if (request.getCategory() != null) record.setCategory(request.getCategory());
        if (request.getDate() != null) record.setDate(request.getDate());
        if (request.getNotes() != null) record.setNotes(request.getNotes());

        record = recordRepository.save(record);
        return recordMapper.toResponse(record);
    }

    @Override
    @Transactional
    public void deleteRecord(Long userId, Long recordId, boolean isAdmin) {
        FinancialRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Record", recordId));

        if (!isAdmin && !record.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You do not have permission to delete this record");
        }

        record.setDeleted(true);
        recordRepository.save(record);
    }
}

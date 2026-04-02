package com.finmanagement.dto.dashboard;

import com.finmanagement.entity.enums.RecordType;

import java.math.BigDecimal;

public class CategoryTotalResponse {

    private String category;
    private RecordType type;
    private BigDecimal total;
    private long count;

    public CategoryTotalResponse() {}

    public CategoryTotalResponse(String category, RecordType type, BigDecimal total, long count) {
        this.category = category;
        this.type = type;
        this.total = total;
        this.count = count;
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public RecordType getType() { return type; }
    public void setType(RecordType type) { this.type = type; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public long getCount() { return count; }
    public void setCount(long count) { this.count = count; }
}

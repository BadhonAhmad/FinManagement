package com.finmanagement.dto.dashboard;

import java.math.BigDecimal;

public class TrendDataResponse {

    private String period;
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal net;

    public TrendDataResponse() {}

    public TrendDataResponse(String period, BigDecimal totalIncome, BigDecimal totalExpenses, BigDecimal net) {
        this.period = period;
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
        this.net = net;
    }

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }

    public BigDecimal getTotalIncome() { return totalIncome; }
    public void setTotalIncome(BigDecimal totalIncome) { this.totalIncome = totalIncome; }

    public BigDecimal getTotalExpenses() { return totalExpenses; }
    public void setTotalExpenses(BigDecimal totalExpenses) { this.totalExpenses = totalExpenses; }

    public BigDecimal getNet() { return net; }
    public void setNet(BigDecimal net) { this.net = net; }
}

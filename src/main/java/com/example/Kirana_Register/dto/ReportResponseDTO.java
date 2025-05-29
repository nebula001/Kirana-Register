package com.example.Kirana_Register.dto;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReportResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String reportType;
    private CurrencyAmounts totalCredits;
    private CurrencyAmounts totalDebits;
    private CurrencyAmounts netFlow;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate;

    // Constructors
    public ReportResponseDTO() {
    }

    public ReportResponseDTO(String reportType, CurrencyAmounts totalCredits,
                             CurrencyAmounts totalDebits, CurrencyAmounts netFlow,
                             LocalDateTime startDate, LocalDateTime endDate) {
        this.reportType = reportType;
        this.totalCredits = totalCredits;
        this.totalDebits = totalDebits;
        this.netFlow = netFlow;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and Setters
    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public CurrencyAmounts getTotalCredits() {
        return totalCredits;
    }

    public void setTotalCredits(CurrencyAmounts totalCredits) {
        this.totalCredits = totalCredits;
    }

    public CurrencyAmounts getTotalDebits() {
        return totalDebits;
    }

    public void setTotalDebits(CurrencyAmounts totalDebits) {
        this.totalDebits = totalDebits;
    }

    public CurrencyAmounts getNetFlow() {
        return netFlow;
    }

    public void setNetFlow(CurrencyAmounts netFlow) {
        this.netFlow = netFlow;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    // Nested class for currency amounts
    public static class CurrencyAmounts implements Serializable {
        private static final long serialVersionUID = 1L;

        private BigDecimal usd;
        private BigDecimal inr;

        public CurrencyAmounts() {
        }

        public CurrencyAmounts(BigDecimal usd, BigDecimal inr) {
            this.usd = usd;
            this.inr = inr;
        }

        public BigDecimal getUsd() {
            return usd;
        }

        public void setUsd(BigDecimal usd) {
            this.usd = usd;
        }

        public BigDecimal getInr() {
            return inr;
        }

        public void setInr(BigDecimal inr) {
            this.inr = inr;
        }
    }
}


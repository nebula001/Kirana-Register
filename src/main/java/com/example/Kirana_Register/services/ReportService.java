package com.example.Kirana_Register.services;

import com.example.Kirana_Register.dto.ReportResponseDTO;
import com.example.Kirana_Register.entities.Transaction;
import com.example.Kirana_Register.repositories.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@Slf4j
public class ReportService {
    @Autowired
    private TransactionRepository transactionRepository;

    @Cacheable(value = "transactionReports", key = "'weeklyReportAdmin'")
    public ReportResponseDTO generateWeeklyReport() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minusWeeks(1);

        return generateReport(start, now, "weekly");
    }

    @Cacheable(value = "transactionReports", key = "'monthlyReportAdmin'")
    public ReportResponseDTO generateMonthlyReport() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.with(TemporalAdjusters.firstDayOfMonth());

        return generateReport(start, now, "monthly");
    }

    @Cacheable(value = "transactionReports", key = "'yearlyReportAdmin'")
    public ReportResponseDTO generateYearlyReport() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.with(TemporalAdjusters.firstDayOfYear());

        return generateReport(start, now, "yearly");
    }

    private ReportResponseDTO generateReport(LocalDateTime start, LocalDateTime end, String reportType) {
        if (start == null || end == null || start.isAfter(end)) {
            log.warn("Invalid start and end date provided");
            throw new IllegalArgumentException("Invalid date range: start and end dates must be non-null and start must be before end");
        }

        List<Transaction> records = transactionRepository.findByTransactionDateBetween(start, end);

        BigDecimal totalCreditUsd = BigDecimal.ZERO;
        BigDecimal totalCreditInr = BigDecimal.ZERO;
        BigDecimal totalDebitUsd = BigDecimal.ZERO;
        BigDecimal totalDebitInr = BigDecimal.ZERO;

        for (Transaction record : records) {
            if ("CREDIT".equalsIgnoreCase(String.valueOf(record.getType()))) {
                totalCreditUsd = totalCreditUsd.add(record.getAmountUsd());
                totalCreditInr = totalCreditInr.add(record.getAmountInr());
            } else if ("DEBIT".equalsIgnoreCase(String.valueOf(record.getType()))) {
                totalDebitUsd = totalDebitUsd.add(record.getAmountUsd());
                totalDebitInr = totalDebitInr.add(record.getAmountInr());
            }
        }

        BigDecimal netFlowUsd = totalCreditUsd.subtract(totalDebitUsd);
        BigDecimal netFlowInr = totalCreditInr.subtract(totalDebitInr);

        // Create DTO objects
        ReportResponseDTO.CurrencyAmounts totalCredits = new ReportResponseDTO.CurrencyAmounts(totalCreditUsd, totalCreditInr);
        ReportResponseDTO.CurrencyAmounts totalDebits = new ReportResponseDTO.CurrencyAmounts(totalDebitUsd, totalDebitInr);
        ReportResponseDTO.CurrencyAmounts netFlow = new ReportResponseDTO.CurrencyAmounts(netFlowUsd, netFlowInr);

        return new ReportResponseDTO(reportType, totalCredits, totalDebits, netFlow, start, end);
    }
}


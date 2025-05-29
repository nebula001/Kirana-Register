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
import java.util.List;

@Service
@Slf4j
public class ReportServiceUser {

    @Autowired
    private TransactionRepository transactionRepository;

    @Cacheable(value = "weeklyReportCache", key = "#userId + '_' + #start.toString() + '_' + #end.toString()")
    public ReportResponseDTO generateWeeklyReportForUser(Long userId, LocalDateTime start, LocalDateTime end) {
        return generateReport(userId, start, end, "WEEKLY");
    }

    @Cacheable(value = "monthlyReportCache", key = "#userId + '_' + #start.toString() + '_' + #end.toString()")
    public ReportResponseDTO generateMonthlyReportForUser(Long userId, LocalDateTime start, LocalDateTime end) {
        return generateReport(userId, start, end, "MONTHLY");
    }

    @Cacheable(value = "yearlyReportCache", key = "#userId + '_' + #start.toString() + '_' + #end.toString()")
    public ReportResponseDTO generateYearlyReportForUser(Long userId, LocalDateTime start, LocalDateTime end) {
        return generateReport(userId, start, end, "YEARLY");
    }

    private ReportResponseDTO generateReport(Long userId, LocalDateTime start, LocalDateTime end, String reportType) {
        if (userId == null) {
            log.warn("No user Id Provided");
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (start == null || end == null || start.isAfter(end)) {
            log.warn("Invalid date range provided");
            throw new IllegalArgumentException("Invalid date range: start must be before end");
        }

        List<Transaction> records = transactionRepository.findByUser_IdAndTransactionDateBetween(userId, start, end);

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

        return new ReportResponseDTO(
                reportType,
                new ReportResponseDTO.CurrencyAmounts(totalCreditUsd, totalCreditInr),
                new ReportResponseDTO.CurrencyAmounts(totalDebitUsd, totalDebitInr),
                new ReportResponseDTO.CurrencyAmounts(netFlowUsd, netFlowInr),
                start,
                end
        );
    }
}

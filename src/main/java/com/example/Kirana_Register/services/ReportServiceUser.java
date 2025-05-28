package com.example.Kirana_Register.services;

import com.example.Kirana_Register.entities.Transaction;
import com.example.Kirana_Register.repositories.TransactionRepository;
import com.example.Kirana_Register.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceUser {
    @Autowired
    private TransactionRepository transactionRepository;

    public Map<String, Object> generateWeeklyReport() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getUser().getId();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minusWeeks(1);

        return generateReport(userId, start, now, "weekly");
    }

    public Map<String, Object> generateMonthlyReport() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getUser().getId();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.with(TemporalAdjusters.firstDayOfMonth());

        return generateReport(userId, start, now, "monthly");
    }

    public Map<String, Object> generateYearlyReport() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getUser().getId();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.with(TemporalAdjusters.firstDayOfYear());

        return generateReport(userId, start, now, "yearly");
    }

    private Map<String, Object> generateReport(Long userId, LocalDateTime start, LocalDateTime end, String reportType) {
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

        Map<String, Object> result = new HashMap<>();
        result.put("reportType", reportType);
        result.put("totalCredits", Map.of("USD", totalCreditUsd, "INR", totalCreditInr));
        result.put("totalDebits", Map.of("USD", totalDebitUsd, "INR", totalDebitInr));
        result.put("netFlow", Map.of("USD", netFlowUsd, "INR", netFlowInr));
        result.put("startDate", start);
        result.put("endDate", end);

        return result;
    }
}

package com.example.Kirana_Register.services;

import com.example.Kirana_Register.entities.Transaction;
import com.example.Kirana_Register.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportGeneratorService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Cacheable(value = "reports", key = "#reportType + '_' + #userId + '_' + #start.toString() + '_' + #end.toString()")
    public String generateReport(Long userId, LocalDateTime start, LocalDateTime end, String reportType) {
        List<Transaction> transactions = transactionRepository.findByUser_IdAndTransactionDateBetween(userId, start, end);

        BigDecimal totalCreditUsd = BigDecimal.ZERO;
        BigDecimal totalCreditInr = BigDecimal.ZERO;
        BigDecimal totalDebitUsd = BigDecimal.ZERO;
        BigDecimal totalDebitInr = BigDecimal.ZERO;

        for (Transaction txn : transactions) {
            boolean isCredit = "CREDIT".equalsIgnoreCase(txn.getType().toString());
            boolean isDebit = "DEBIT".equalsIgnoreCase(txn.getType().toString());

            if (isCredit) {
                totalCreditUsd = totalCreditUsd.add(txn.getAmountUsd());
                totalCreditInr = totalCreditInr.add(txn.getAmountInr());
            } else if (isDebit) {
                totalDebitUsd = totalDebitUsd.add(txn.getAmountUsd());
                totalDebitInr = totalDebitInr.add(txn.getAmountInr());
            }
        }

        BigDecimal netFlowUsd = totalCreditUsd.subtract(totalDebitUsd);
        BigDecimal netFlowInr = totalCreditInr.subtract(totalDebitInr);

        return formatReport(reportType, start, end, totalCreditUsd, totalCreditInr, totalDebitUsd, totalDebitInr, netFlowUsd, netFlowInr);
    }

    private String formatReport(
            String reportType,
            LocalDateTime start,
            LocalDateTime end,
            BigDecimal totalCreditUsd,
            BigDecimal totalCreditInr,
            BigDecimal totalDebitUsd,
            BigDecimal totalDebitInr,
            BigDecimal netFlowUsd,
            BigDecimal netFlowInr
    ) {
        return String.format("Report Type : %s%n" +
                        "Start Date  : %s%n" +
                        "End Date    : %s%n" +
                        "Total Credits: USD %s | INR %s%n" +
                        "Total Debits : USD %s | INR %s%n" +
                        "Net Flow     : USD %s | INR %s%n",
                reportType.toUpperCase(),
                start,
                end,
                totalCreditUsd,
                totalCreditInr,
                totalDebitUsd,
                totalDebitInr,
                netFlowUsd,
                netFlowInr
        );
    }
}


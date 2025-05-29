//package com.example.Kirana_Register.services;
//
//import com.example.Kirana_Register.entities.Transaction;
//import com.example.Kirana_Register.repositories.TransactionRepository;
//import com.example.Kirana_Register.security.CustomUserDetails;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.time.temporal.TemporalAdjusters;
//import java.util.List;
//import java.util.UUID;
//
//@Service
//public class ReportServiceKafka {
//    @Autowired
//    private TransactionRepository transactionRepository;
//
//    @Autowired
//    private KafkaTemplate<String, String> stringKafkaTemplate;
//
//    @Value("${kafka.topics.string-reports}")
//    private String stringReportsTopic;
//
//    public void generateWeeklyReport() {
//        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        Long userId = userDetails.getUser().getId();
//        LocalDateTime now = LocalDateTime.now();
//        LocalDateTime start = now.minusWeeks(1);
//
//        generateAndSendReport(userId, start, now, "weekly");
//    }
//
//    public void generateMonthlyReport() {
//        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        Long userId = userDetails.getUser().getId();
//        LocalDateTime now = LocalDateTime.now();
//        LocalDateTime start = now.with(TemporalAdjusters.firstDayOfMonth());
//
//        generateAndSendReport(userId, start, now, "monthly");
//    }
//
//    public void generateYearlyReport() {
//        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        Long userId = userDetails.getUser().getId();
//        LocalDateTime now = LocalDateTime.now();
//        LocalDateTime start = now.with(TemporalAdjusters.firstDayOfYear());
//
//        generateAndSendReport(userId, start, now, "yearly");
//    }
//
//    private void generateAndSendReport(Long userId, LocalDateTime start, LocalDateTime end, String reportType) {
//        List<Transaction> records = transactionRepository.findByUser_IdAndTransactionDateBetween(userId, start, end);
//
//        BigDecimal totalCreditUsd = BigDecimal.ZERO;
//        BigDecimal totalCreditInr = BigDecimal.ZERO;
//        BigDecimal totalDebitUsd = BigDecimal.ZERO;
//        BigDecimal totalDebitInr = BigDecimal.ZERO;
//
//        for (Transaction record : records) {
//            if ("CREDIT".equalsIgnoreCase(String.valueOf(record.getType()))) {
//                totalCreditUsd = totalCreditUsd.add(record.getAmountUsd());
//                totalCreditInr = totalCreditInr.add(record.getAmountInr());
//            } else if ("DEBIT".equalsIgnoreCase(String.valueOf(record.getType()))) {
//                totalDebitUsd = totalDebitUsd.add(record.getAmountUsd());
//                totalDebitInr = totalDebitInr.add(record.getAmountInr());
//            }
//        }
//
//        BigDecimal netFlowUsd = totalCreditUsd.subtract(totalDebitUsd);
//        BigDecimal netFlowInr = totalCreditInr.subtract(totalDebitInr);
//
//        // String report
//        String stringReport = String.format(
//                "Report Type: %s \n Start Date: %s \n End Date: %s \n Total Credits: USD %s, INR %s \n Total Debits: USD %s, INR %s \n Net Flow: USD %s, INR %s",
//                reportType, start, end, totalCreditUsd, totalCreditInr, totalDebitUsd, totalDebitInr, netFlowUsd, netFlowInr
//        );
//        stringKafkaTemplate.send(stringReportsTopic, UUID.randomUUID().toString(), stringReport);
//        System.out.println("Sent string report to Kafka: " + stringReport);
//    }
//}
//
//

package com.example.Kirana_Register.services;

import com.example.Kirana_Register.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.UUID;

@Service
public class ReportServiceKafka {

    @Autowired
    private ReportGeneratorService reportGeneratorService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.topics.string-reports}")
    private String kafkaTopic;

    public void generateWeeklyReport() {
        generateAndSendReport("weekly", LocalDateTime.now().minusWeeks(1), LocalDateTime.now());
    }

    public void generateMonthlyReport() {
        LocalDateTime now = LocalDateTime.now();
        generateAndSendReport("monthly", now.with(TemporalAdjusters.firstDayOfMonth()), now);
    }

    public void generateYearlyReport() {
        LocalDateTime now = LocalDateTime.now();
        generateAndSendReport("yearly", now.with(TemporalAdjusters.firstDayOfYear()), now);
    }

    private void generateAndSendReport(String reportType, LocalDateTime start, LocalDateTime end) {
        Long userId = getCurrentUserId();
        String report = reportGeneratorService.generateReport(userId, start, end, reportType);
        kafkaTemplate.send(kafkaTopic, UUID.randomUUID().toString(), report);
        System.out.println("✅ Kafka report sent: \n" + report);
    }

    private Long getCurrentUserId() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userDetails.getUser().getId();
    }
}

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

package com.example.Kirana_Register.controllers;

import com.example.Kirana_Register.services.ReportServiceKafka;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kafka/reports")
public class KafkaReportController {
    @Autowired
    private ReportServiceKafka reportServiceKafka;


    @PostMapping("/weekly")
    public String generateWeeklyReport() {
        reportServiceKafka.generateWeeklyReport();
        return "Weekly report sent to Kafka";
    }


    @PostMapping("/monthly")
    public String generateMonthlyReport() {
        reportServiceKafka.generateMonthlyReport();
        return "Monthly report sent to Kafka";
    }


    @PostMapping("/yearly")
    public String generateYearlyReport() {
        reportServiceKafka.generateYearlyReport();
        return "Yearly report sent to Kafka";
    }
}

package com.example.Kirana_Register.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ReportProducer {
    private final KafkaTemplate<String, String> stringKafkaTemplate;
    @Value("${kafka.topics.string-reports}")
    private String stringReportsTopic;


    public ReportProducer(KafkaTemplate<String, String> stringKafkaTemplate) {
        this.stringKafkaTemplate = stringKafkaTemplate;

    }

    public void sendStringReport(String reportContent) {
        String key = UUID.randomUUID().toString();
        stringKafkaTemplate.send(stringReportsTopic, key, reportContent);
        System.out.println("Sent string report: " + reportContent);
    }
}

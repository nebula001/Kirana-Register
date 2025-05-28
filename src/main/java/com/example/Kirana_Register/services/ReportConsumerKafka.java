package com.example.Kirana_Register.services;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ReportConsumerKafka {

    @KafkaListener(topics = "${kafka.topics.string-reports}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeStringReport(String report) {
        System.out.println("Received string report: " + report);
    }
}




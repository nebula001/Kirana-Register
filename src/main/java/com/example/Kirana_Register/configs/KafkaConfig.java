package com.example.Kirana_Register.configs;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaConfig {
    @Value("${kafka.topics.string-reports}")
    private String stringReportsTopic;

    @Bean
    public NewTopic stringReportsTopic() {
        return TopicBuilder.name(stringReportsTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public KafkaTemplate<String, String> stringKafkaTemplate(ProducerFactory<String, String> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}



package com.enterprise.inventory_service.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.awt.*;

@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;


    public void send(
            String topic,
            String key,
            String message
    ) {

        try {
            kafkaTemplate.send(topic,key,message).get();

        } catch (Exception e) {
            throw new RuntimeException("Failed to publish Kafka event: " + topic, e);
        }
    }
}
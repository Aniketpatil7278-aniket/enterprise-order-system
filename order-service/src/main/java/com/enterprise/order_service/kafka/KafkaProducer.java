package com.enterprise.order_service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void send(String topic,
                     String key,
                     String message){
        kafkaTemplate
                .send(topic,key,message)
                .whenComplete((result, execption)->{
                    if(execption !=null){
                        log.error("Kafka message failed. Topic={}, key={}",
                                topic,key,execption );
                    }else {
                        log.info("Kafka message sent. Topic={}, key={}",
                                topic,
                                key);
                    }
                });
    }
}

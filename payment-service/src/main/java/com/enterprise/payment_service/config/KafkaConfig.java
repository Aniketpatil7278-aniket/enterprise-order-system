package com.enterprise.payment_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic paymentCompletedTopic(){
        return new NewTopic(
                "payment.completed",3,(short)1
        );
    }

    @Bean
    public NewTopic paymentFailedTopic(){
        return new NewTopic(
                "payment.failed",3 ,(short)1
        );
    }

    @Bean
    public NewTopic orderCreatedTopic(){
        return new NewTopic(
                "orderCreatedTopic", 3,(short)1
        );
    }
}

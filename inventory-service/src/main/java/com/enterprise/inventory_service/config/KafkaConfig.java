package com.enterprise.inventory_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.ExponentialBackOff;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic orderCreatedTopic() {
        return new NewTopic(
                "order.created",
                3,
                (short) 1
        );
    }


    @Bean
    public NewTopic inventoryReservedTopic() {
        return new NewTopic(
                "inventory.reserved",
                3,
                (short) 1
        );
    }


    @Bean
    public NewTopic inventoryReleasedTopic() {
        return new NewTopic(
                "inventory.released",
                3,
                (short) 1
        );
    }


    @Bean
    public NewTopic paymentFailedTopic() {
        return new NewTopic(
                "payment.failed",
                3,
                (short) 1
        );
    }


    // EXPONENTIAL BACKOFF

    @Bean
    public DefaultErrorHandler errorHandler() {
        ExponentialBackOff backOff =
                new ExponentialBackOff(
                        1000L,
                        2.0
                );


        backOff.setMaxElapsedTime(
                30000L
        );


        return new DefaultErrorHandler(
                backOff
        );
    }


    // KAFKA LISTENER

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(ConsumerFactory<String, String>
                    consumerFactory,
            DefaultErrorHandler errorHandler) {

        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);


        factory.setCommonErrorHandler(errorHandler);


        return factory;
    }
}
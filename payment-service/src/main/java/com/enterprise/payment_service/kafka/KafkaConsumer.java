package com.enterprise.payment_service.kafka;

import com.enterprise.payment_service.dto.PaymentRequestDto;
import com.enterprise.payment_service.event.OrderCreatedEvent;
import com.enterprise.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {
    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;


    @KafkaListener(
            topics = "order.created",
            groupId = "payment-service-group")
    public void consumeOrderCreated(String message){
        try{
            log.info("Received order.created event: {}" ,message);

            OrderCreatedEvent event=objectMapper.readValue(message, OrderCreatedEvent.class);

            PaymentRequestDto request=PaymentRequestDto.builder()
                    .orderId(event.getOrderId())
                    .amount(event.getTotal())
                    .idempotencyKey("ORDER-" +event.getOrderId())
                    .build();

            paymentService.createPayment(request);

            log.info("Payment processed for order: {}" ,event.getOrderId());

        }catch (Exception e){
            log.error("Failed to process order.created event" ,e);
            throw  new RuntimeException(e);

        }
    }

}

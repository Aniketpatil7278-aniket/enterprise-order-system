package com.enterprise.order_service.kafka;


import com.enterprise.order_service.event.PaymentEvent;
import com.enterprise.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;


@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {
    private final OrderService orderService;

    private final ObjectMapper objectMapper;

    //paymnet pass
    @KafkaListener(
            topics = "payment.completed",
            groupId = "order-service-group"
    )

    public void paymentCompleted(String message){
        try {
            PaymentEvent event=objectMapper.readValue(message, PaymentEvent.class);

            log.info("Payment completed for order {}",
                    event.getOrderId());

            orderService.handlePaymentCompleted(event);

        }catch (Exception e){
            log.error("Error processing payment.completed",e);

            throw new RuntimeException(e);

        }
    }

    //payment failed
    @KafkaListener(
            topics = "payment.failed",
            groupId = "order-service-group")
    public void paymentFailed(String message) {
        try {
            PaymentEvent event = objectMapper.readValue(
                    message,
                    PaymentEvent.class
            );

            log.info("Payment failed for order {}", event.getOrderId());

            orderService.handlePaymentFailed(event);

        } catch (Exception e) {
            log.error("Error processing payment.failed", e);
            throw new RuntimeException(e);
        }
    }




}

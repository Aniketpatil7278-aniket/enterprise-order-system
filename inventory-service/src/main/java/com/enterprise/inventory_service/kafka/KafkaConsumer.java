package com.enterprise.inventory_service.kafka;

import com.enterprise.inventory_service.event.OrderEvent;
import com.enterprise.inventory_service.event.PaymentEvent;
import com.enterprise.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {
    private final InventoryService inventoryService;
    private final ObjectMapper objectMapper;


    //order created in kafka
    @KafkaListener(topics = "order.created",
            groupId = "inventory-service-group")
    public void  consumeOrderCreated(String message){
        try {
            log.info("Received order.created: {}", message);

            OrderEvent event=objectMapper.readValue(message, OrderEvent.class);

            if(event.getItems() == null || event.getItems().isEmpty()){
                log.warn("Order {} has no items" ,event.getOrderId());
                return;
            }

            for (OrderEvent.OrderItemEvent item : event.getItems()){
                inventoryService.reserveStock(
                        event.getOrderId(),
                        item.getProductId(),
                        item.getQuantity()
                );
            }
            log.info("Inventory reserved for order: {}", event.getOrderId());

        }catch (Exception e){
            log.error("Failed to process order.created",e);
            throw new RuntimeException(e);

        }
    }

    //payment failed kafka
    @KafkaListener(topics = "payment.failed",
            groupId = "inventory-service-group")
    public void consumePaymentFailed(String message){
        try {
            log.info("Received payment.failed: {}", message);

            PaymentEvent event=objectMapper.readValue(message, PaymentEvent.class);
            inventoryService.releaseOrderInventory(event.getOrderId());

            log.info("Compensation required for order: {}", event.getOrderId());

        }catch (Exception e){
            log.error("Failed to process payment.failed", e);
            throw new RuntimeException(e);

        }
    }
}

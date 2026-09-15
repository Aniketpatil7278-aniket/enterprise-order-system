package com.enterprise.order_service.service;


import com.enterprise.order_service.dto.OrderItemRequestDto;
import com.enterprise.order_service.dto.OrderRequestDto;
import com.enterprise.order_service.dto.OrderResponseDto;
import com.enterprise.order_service.entity.Order;
import com.enterprise.order_service.entity.OrderItem;
import com.enterprise.order_service.entity.OutboxEvent;
import com.enterprise.order_service.enums.OrderStatus;
import com.enterprise.order_service.event.OrderEvent;
import com.enterprise.order_service.event.PaymentEvent;
import com.enterprise.order_service.exception.OrderNotFoundException;
import com.enterprise.order_service.repository.OrderRepository;
import com.enterprise.order_service.repository.OutboxEventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.core.JacksonException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    // ENTITY → RESPONSE
    private OrderResponseDto mapToResponse(Order order){
        List<OrderResponseDto.OrderItemResponse> items=order.getItems()
                .stream()
                .map(item->OrderResponseDto.OrderItemResponse
                        .builder()
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .build())
                .toList();

        return OrderResponseDto.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .status(order.getStatus())
                .total(order.getTotal())
                .idempotencyKey(order.getIdempotencyKey())
                .createdAt(order.getCreatedAt())
                .updateAt(order.getUpdatedAt())
                .items(items)
                .build();
    }

    //CREATE EVENT
    private OrderEvent createOrderCreatedEvent(Order order){
        List<OrderEvent.OrderItemEvent> items=
                order.getItems().stream()
                        .map(item->OrderEvent.OrderItemEvent
                                .builder()
                                .productId(item.getProductId())
                                .quantity(item.getQuantity())
                                .price(item.getPrice())
                                .build()
                        )
                        .toList();
        return OrderEvent.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .total(order.getTotal())
                .items(items)
                .build();

    }

    //____________________create order________________________
    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto request, String idempotencyKey){
        if(idempotencyKey == null || idempotencyKey.isBlank()){
            throw new IllegalArgumentException("Idempotency-Key header is required");
        }

        //check the duplicate order
        var existingOrder=orderRepository.findByIdempotencyKey(idempotencyKey);

        if(existingOrder.isPresent()){
            log.info("Duplicate order request detected: {}",idempotencyKey);

            return mapToResponse(existingOrder.get());
        }

        //create the order
        Order order=Order.builder()
                .userId(request.getUserId())
                .status(OrderStatus.PAYMENT_PENDING)
                .total(request.getTotal())
                .idempotencyKey(idempotencyKey)
                .build();

        //Add the item
        for (OrderItemRequestDto itemRequestDto:request.getItems()){
            OrderItem item=OrderItem.builder()
                    .productId(itemRequestDto.getProductId())
                    .quantity(itemRequestDto.getQuantity())
                    .price(itemRequestDto.getPrice())
                    .build();

            order.addItem(item);
        }

        //save
        Order savedOrder = orderRepository.save(order);

        //create Kafka evnet
        OrderEvent event=createOrderCreatedEvent(savedOrder);

        try{
            String payload=objectMapper.writeValueAsString(event);

            //transactional outbox
            OutboxEvent outboxEvent=OutboxEvent.builder()
                    .eventType("order.created")
                    .aggregateId(savedOrder.getId().toString())
                    .payload(payload)
                    .published(false)
                    .createdAt(LocalDateTime.now())
                    .build();

            outboxEventRepository.save(outboxEvent);

        }catch (JacksonException  exception) {

            throw new RuntimeException("Failed to create order event", exception);
        }

        return mapToResponse(savedOrder);
    }


    //__________________________ GET ORDER BY ID_______________________
    public OrderResponseDto getOrderById(Long id){
        Order order=orderRepository.findById(id)
                .orElseThrow(()-> new OrderNotFoundException("Order not found with id: " +id));

        return mapToResponse(order);
    }

    //____________________________payment success___________________________
    @Transactional
    public void handlePaymentCompleted(PaymentEvent event){
        Order order=orderRepository.findById(event.getOrderId()
        ).orElseThrow(()->new
                OrderNotFoundException("Order not found with id " +event.getOrderId())
        );

        //Idempotent event processing
        if(order.getStatus() == OrderStatus.PAYMENT_COMPLETED){
            log.info("Payment event already processed for order {}", order.getId());
            return;
        }

        order.setStatus(OrderStatus.PAYMENT_COMPLETED);
        orderRepository.save(order);

        log.info("Order {} payment completed" , order.getId());

    }

    //________________________paymnet Failure_____________________
    @Transactional
    public void handlePaymentFailed(PaymentEvent event){
        Order order=orderRepository.findById(event.getOrderId())
                .orElseThrow(()->new OrderNotFoundException("Order not found with id :" +event.getOrderId()));

        // Already cancelled
        if(order.getStatus() == OrderStatus.CANCELLED){
            return;
        }
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        //Saga compensation
        cancelOrder(order.getId());
    }


    //_________________cancle order_____________________
    public void cancelOrder(Long orderId){
        Order order=orderRepository.findById(orderId)
                .orElseThrow(()-> new OrderNotFoundException("Order not found with id " +orderId));

        order.setStatus(OrderStatus.CANCELLED);

        orderRepository.save(order);

    }



}

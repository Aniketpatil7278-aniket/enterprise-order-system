package com.enterprise.inventory_service.event;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderEvent {

    private Long orderId;

    private Long userId;

    private BigDecimal total;

    private List<OrderItemEvent> items;


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemEvent{
        private Long productId;

        private Integer quantity;

        private BigDecimal price;

    }
}

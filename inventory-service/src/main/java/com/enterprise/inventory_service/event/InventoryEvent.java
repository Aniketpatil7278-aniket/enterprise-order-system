package com.enterprise.inventory_service.event;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryEvent {
    private Long orderId;

    private  Long productId;

    private Integer quantity;

    private String status;

    private String message;
}

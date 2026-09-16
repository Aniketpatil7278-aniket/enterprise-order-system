package com.enterprise.inventory_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReleaseStockResponseDto {

    private String message;

    private Long orderId;

    private Long productId;

    private Integer quantity;
}

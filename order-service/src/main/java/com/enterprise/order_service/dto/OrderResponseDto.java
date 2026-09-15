package com.enterprise.order_service.dto;



import com.enterprise.order_service.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponseDto {

    private Long id;

    private Long userId;

    private OrderStatus status;

    private BigDecimal total;

    private String idempotencyKey;

    private LocalDateTime createdAt;

    private LocalDateTime updateAt;

    private List<OrderItemResponse> items;

    @Data
    @Builder
    public static class OrderItemResponse{
        private Long productId;

        private Integer quantity;

        private BigDecimal price;
    }
}



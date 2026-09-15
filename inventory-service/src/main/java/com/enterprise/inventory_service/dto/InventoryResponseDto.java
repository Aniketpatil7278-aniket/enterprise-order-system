package com.enterprise.inventory_service.dto;

import com.enterprise.inventory_service.enums.InventoryStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryResponseDto {

    private Long id;

    private  Long productId;

    private Integer availableQuantity;

    private Integer reservedQuantity;

    private InventoryStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

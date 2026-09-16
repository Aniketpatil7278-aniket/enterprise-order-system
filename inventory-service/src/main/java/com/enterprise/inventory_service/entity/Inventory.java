package com.enterprise.inventory_service.entity;

import com.enterprise.inventory_service.enums.InventoryStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_inventory_product",
                        columnNames = "product_id"
                )
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false , name = "product_id")
    private Long productId;

    @Column(nullable = false)
    private  Integer availableQuantity;

    @Column(nullable = false)
    private Integer reservedQuantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InventoryStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate(){
        createdAt=LocalDateTime.now();
        updatedAt=LocalDateTime.now();

        if(reservedQuantity == null){
            reservedQuantity=0;
        }

        if(status == null){
            status=availableQuantity !=null
                    &&availableQuantity >0
                    ?InventoryStatus.AVAILABLE
                    :InventoryStatus.OUT_OF_STOCK;
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();

        if (availableQuantity != null && availableQuantity > 0) {
            status = InventoryStatus.AVAILABLE;
        } else {
            status = InventoryStatus.OUT_OF_STOCK;
        }
    }

}

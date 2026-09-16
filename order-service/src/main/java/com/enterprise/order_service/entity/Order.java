package com.enterprise.order_service.entity;


import com.enterprise.order_service.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders",
        uniqueConstraints = {@UniqueConstraint(name = "uk_order_idempotency_key",
                columnNames = "idempotency_key")}
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id",nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false, precision = 12 , scale = 2)
    private BigDecimal total;

    @Column(name = "idempotency_key" , unique = true, nullable = false)
    private String idempotencyKey;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private  LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items=new ArrayList<>();

    @PrePersist
    public void beforeCreate(){
        LocalDateTime now= LocalDateTime.now();

        createdAt=now;
        updatedAt=now;
    }

    @PreUpdate
    public void beforeUpdate(){
        updatedAt=LocalDateTime.now();
    }

    public void addItem(OrderItem item){
        items.add(item);
        item.setOrder(this);
    }
}


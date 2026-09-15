package com.enterprise.inventory_service.event;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentEvent {
    private Long paymentId;

    private Long orderId;

    private String status;

    private String transactionId;
}

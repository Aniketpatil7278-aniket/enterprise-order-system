package com.enterprise.order_service.event;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentEvent {

    private Long orderId;

    private Long paymentId;

    private String status;

    private String transactionId;
}

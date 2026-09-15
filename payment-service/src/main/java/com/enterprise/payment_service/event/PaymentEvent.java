package com.enterprise.payment_service.event;

import com.enterprise.payment_service.enums.PaymentStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentEvent {
    private Long paymentId;

    private Long orderId;

    private PaymentStatus  status;

    private String transactionId;
}

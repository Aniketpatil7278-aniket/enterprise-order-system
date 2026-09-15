package com.enterprise.payment_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequestDto {

    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotNull(message = "amount is required.")
    @DecimalMin(value = "0.01",message = "Amount must be greater than zero")
    private BigDecimal amount;

    private String idempotencyKey;
}

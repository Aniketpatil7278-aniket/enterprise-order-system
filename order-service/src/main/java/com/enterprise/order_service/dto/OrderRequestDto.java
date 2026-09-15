package com.enterprise.order_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderRequestDto {

    @NotNull(message = "User Id id required.")
    private Long userId;

    @NotNull(message = "Total amount is required.")
    @DecimalMin(value = "0.01", message = "Total must be greater than 0")
    private BigDecimal total;

    @NotEmpty(message = "Order must contain at least 1 ite")
    @Valid
    private List<OrderItemRequestDto> items;


}

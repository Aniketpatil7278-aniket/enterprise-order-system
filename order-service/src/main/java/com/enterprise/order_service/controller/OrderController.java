package com.enterprise.order_service.controller;




import com.enterprise.order_service.dto.OrderRequestDto;
import com.enterprise.order_service.dto.OrderResponseDto;
import com.enterprise.order_service.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    //POST
    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(
            @RequestHeader(name = "Idempotency-Key" , required = false) String idempotencyKey,
            @Valid
            @RequestBody OrderRequestDto requestDto
    ){
        OrderResponseDto res=orderService.createOrder(requestDto, idempotencyKey);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(res);
    }

    // GET /orders/{id}
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrder(@PathVariable Long id){
        return ResponseEntity.ok(orderService.getOrderById(id));
    }
}


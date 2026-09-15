package com.enterprise.payment_service.controller;

import com.enterprise.payment_service.dto.PaymentRequestDto;
import com.enterprise.payment_service.dto.PaymentResponseDto;
import com.enterprise.payment_service.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    //create the payment
   //post payment
   @PostMapping
   @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponseDto createPayment(@Valid @RequestBody PaymentRequestDto requestDto,
                                            @RequestHeader(value = "Idempotency-Key",
                                            required = false)
                                            String headerIdempotencyKey){
       // Prefer Idempotency-Key header.If header is not provided, use request body value.
       if(headerIdempotencyKey !=null && !headerIdempotencyKey.isBlank()){
           requestDto.setIdempotencyKey(headerIdempotencyKey);
       }
       //calling the services method
       return paymentService.createPayment(requestDto);

    }



    //GEt paymnet By id
    @GetMapping("/{id}")
    public PaymentResponseDto getPayment(@PathVariable Long id){
       return paymentService.getPayment(id);
    }

    //get payment by order Id
    @GetMapping("order/{orderId}")
    public PaymentResponseDto getPaymentByOrderId(@PathVariable Long orderId){
       return paymentService.getPaymentByOrderId(orderId);
    }



}

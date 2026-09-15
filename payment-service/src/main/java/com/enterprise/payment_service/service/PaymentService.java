package com.enterprise.payment_service.service;

import com.enterprise.payment_service.dto.PaymentRequestDto;
import com.enterprise.payment_service.dto.PaymentResponseDto;
import com.enterprise.payment_service.entity.OutboxEvent;
import com.enterprise.payment_service.entity.Payment;
import com.enterprise.payment_service.enums.PaymentStatus;
import com.enterprise.payment_service.event.PaymentEvent;
import com.enterprise.payment_service.exception.DuplicatePaymentException;
import com.enterprise.payment_service.exception.PaymentNotFoundException;
import com.enterprise.payment_service.repository.OutboxEventRepository;
import com.enterprise.payment_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    //map entity to DTO
    private PaymentResponseDto mapToResponse(Payment payment){
        return PaymentResponseDto.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .transactionId(payment.getTransactionId())
                .idempotencyKey(payment.getIdempotencyKey())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }


    //_____________create payments ________________________
    @Transactional
    public PaymentResponseDto createPayment(PaymentRequestDto request) {
        String idempotencyKey = request.getIdempotencyKey();

        //Check duplicate idempotency key
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            var existingPayment = paymentRepository.findByIdempotencyKey(idempotencyKey);
            if (existingPayment.isPresent()) {
                return mapToResponse(existingPayment.get());
            }
        }

        //check the duplicate order
        if (paymentRepository.existsByOrderId(request.getOrderId())) {
            throw new DuplicatePaymentException("Payment already exists for order: " + request.getOrderId());

        }


        //create the payment
        Payment payment=Payment.builder()
                .orderId(request.getOrderId())
                .amount(request.getAmount())
                .status(PaymentStatus.PENDING)
                .idempotencyKey(idempotencyKey)
                .build();

        Payment savedPayment =paymentRepository.save(payment);


        //Simulate payment processing
        boolean paymentSuccessful=processPayment(savedPayment );

        if(paymentSuccessful){
            savedPayment .setStatus(PaymentStatus.SUCCESS);
            savedPayment .setTransactionId("TXN" + UUID.randomUUID());
        }else {
            savedPayment .setStatus(PaymentStatus.FAILED);
        }

        savedPayment=paymentRepository.save(savedPayment );

        // Create Kafka event
        createOutboxEvent(savedPayment);;
        return mapToResponse(savedPayment);

    }

    //process payment
    private boolean processPayment(Payment payment){
//        //bank getway
////        Razorpay, paypal
//        return true;
        return payment.getAmount().compareTo(BigDecimal.valueOf(5000)) <= 0;
    }

    //create the outbox event
    private void createOutboxEvent(Payment payment){
        PaymentEvent event=PaymentEvent.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrderId())
                .status(payment.getStatus())
                .transactionId(payment.getTransactionId())
                .build();

        try {
            String payload=objectMapper.writeValueAsString(event);
            String eventType;

            if(payment.getStatus() == PaymentStatus.SUCCESS){
                eventType="payment.completed";
            }else {
                eventType="payment.failed";
            }

            OutboxEvent outboxEvent=OutboxEvent.builder()
                    .eventType(eventType)
                    .aggregateId(payment.getOrderId().toString())
                    .payload(payload)
                    .published(false)
                    .createdAt(LocalDateTime.now())
                    .build();

            outboxEventRepository.save(outboxEvent);

        }catch (JacksonException e){
            throw  new RuntimeException("Failed to serialize payment event" ,e);

        }
    }

    //get payment
    @Transactional(readOnly = true)
    public PaymentResponseDto getPayment(Long paymentId){
        Payment payment=paymentRepository.findById(paymentId)
                .orElseThrow(()-> new PaymentNotFoundException("Payment not found: " +paymentId));

        return mapToResponse(payment);

    }

    //get payment by order
    @Transactional(readOnly = true)
    public PaymentResponseDto getPaymentByOrderId(Long orderId){
        Payment payment=paymentRepository.findByOrderId(orderId)
                .orElseThrow(()->new PaymentNotFoundException("Payment not found for order : " +orderId));

        return mapToResponse(payment);

    }
}

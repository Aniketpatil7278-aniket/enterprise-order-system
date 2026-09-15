package com.enterprise.inventory_service.service;

import com.enterprise.inventory_service.dto.InventoryRequestDto;
import com.enterprise.inventory_service.dto.InventoryResponseDto;
import com.enterprise.inventory_service.entity.Inventory;
import com.enterprise.inventory_service.entity.OutboxEvent;
import com.enterprise.inventory_service.enums.InventoryStatus;
import com.enterprise.inventory_service.event.InventoryEvent;
import com.enterprise.inventory_service.event.InventoryReservation;
import com.enterprise.inventory_service.exception.InsufficientStockException;
import com.enterprise.inventory_service.exception.InventoryNotFoundException;
import com.enterprise.inventory_service.repository.InventoryRepository;
import com.enterprise.inventory_service.repository.InventoryReservationRepository;
import com.enterprise.inventory_service.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;
    private final InventoryReservationRepository reservationRepository;




    //map entity to res DTO
    private InventoryResponseDto mapToResponse(Inventory inventory){
        return  InventoryResponseDto.builder()
                .id(inventory.getId())
                .productId(inventory.getProductId())
                .availableQuantity(inventory.getAvailableQuantity())
                .reservedQuantity(inventory.getReservedQuantity())
                .status(inventory.getStatus())
                .createdAt(inventory.getCreatedAt())
                .updatedAt(inventory.getUpdatedAt())
                .build();
    }

    //create the inventory event
    public void createInventoryEvent(Long orderId,
                                     Long productId,
                                     Integer quantity,
                                     String status,
                                     String message){
        InventoryEvent event=InventoryEvent.builder()
                .orderId(orderId)
                .productId(productId)
                .quantity(quantity)
                .status(status)
                .message(message)
                .build();

        try{
            String payload=objectMapper.writeValueAsString(event);
            String eventType;

            if ("RESERVED".equals(status)) {
                eventType = "inventory.reserved";
            } else {
                eventType = "inventory.released";
            }

            OutboxEvent outboxEvent=OutboxEvent.builder()
                    .eventType(eventType)
                    .aggregateId(orderId.toString())
                    .payload(payload)
                    .published(false)
                    .createdAt(LocalDateTime.now())
                    .build();

            outboxEventRepository.save(outboxEvent);


        }catch (JacksonException e){
            throw new RuntimeException("Failed to create inventory event", e);

        }

    }

    //__________________create inventory______________________
    @Transactional
    public InventoryResponseDto createInventory(InventoryRequestDto requestDto){
        if(inventoryRepository.existsByProductId(requestDto.getProductId())){
            throw new IllegalArgumentException("Inventory already exists for product: " +requestDto.getProductId());
        }

        Inventory inventory=Inventory.builder()
                .productId(requestDto.getProductId())
                .availableQuantity(requestDto.getQuantity())
                .reservedQuantity(0)
                .status(requestDto.getQuantity() >0
                        ? InventoryStatus.AVAILABLE
                        :InventoryStatus.OUT_OF_STOCK)
                .build();

        Inventory savedInventory=inventoryRepository.save(inventory);

        return mapToResponse(savedInventory);
    }

    //get Inventory
    @Transactional(readOnly = true)
    public InventoryResponseDto getInventory(Long productId){
        Inventory inventory=inventoryRepository.findByProductId(productId)
                .orElseThrow(()->new InventoryNotFoundException("Inventory not found for product: " +productId));

        return mapToResponse(inventory);
    }


    //_________reserve stock

    @Transactional
    public void reserveStock(
            Long orderId,
            Long productId,
            Integer quantity
    ) {

        // Prevent duplicate reservation
        if (reservationRepository.existsByOrderIdAndProductId(orderId, productId)) {
            return;
        }


        Inventory inventory = inventoryRepository.findByProductId(productId)
                        .orElseThrow(() -> new InventoryNotFoundException("Inventory not found for product: "
                                                + productId));

        // Check stock
        if (inventory.getAvailableQuantity() < quantity) {
            throw new InsufficientStockException("Insufficient stock for product: " + productId);
        }


        // Reserve
        inventory.setAvailableQuantity(inventory.getAvailableQuantity() - quantity);

        inventory.setReservedQuantity(inventory.getReservedQuantity() + quantity);

        inventoryRepository.save(inventory);


        // Save reservation

        InventoryReservation reservation = InventoryReservation.builder()
                                        .orderId(orderId)
                                        .productId(productId)
                                        .quantity(quantity)
                                        .released(false)
                                        .build();


        reservationRepository.save(reservation);


        // Kafka event
        createInventoryEvent(
                orderId,
                productId,
                quantity,
                "RESERVED",
                "Inventory reserved successfully"
        );
    }

    //releaseOrderInventory
    @Transactional
    public void releaseOrderInventory(Long orderId) {

        var reservations = reservationRepository.findByOrderIdAndReleasedFalse(orderId);

        for (InventoryReservation reservation : reservations) {

            Inventory inventory = inventoryRepository.findByProductId(reservation.getProductId())
                            .orElseThrow(() -> new InventoryNotFoundException(
                                            "Inventory not found for product: "
                                                    + reservation.getProductId()
                                    )
                            );

            // Restore stock
            inventory.setReservedQuantity(inventory.getReservedQuantity() - reservation.getQuantity());

            inventory.setAvailableQuantity(inventory.getAvailableQuantity() + reservation.getQuantity());

            inventoryRepository.save(inventory);

            // Mark reservation released
            reservation.setReleased(true);
            reservation.setReleasedAt(LocalDateTime.now());

            reservationRepository.save(reservation);

            // Event
            createInventoryEvent(
                    orderId,
                    reservation.getProductId(),
                    reservation.getQuantity(),
                    "RELEASED",
                    "Inventory released because payment failed");
        }
    }

    //Release stock
    @Transactional
    public void releaseStock(Long orderId,
                             Long productId,
                             Integer quantity){
        Inventory inventory=inventoryRepository.findByProductId(productId)
                .orElseThrow(()-> new InventoryNotFoundException("Insufficient stock for product: " +productId));

        // Prevent invalid release
        int actualRelease = Math.min(quantity, inventory.getAvailableQuantity());

        //Release
        inventory.setAvailableQuantity(inventory.getReservedQuantity() - actualRelease);

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() + actualRelease);

        inventoryRepository.save(inventory);
        createInventoryEvent(orderId, productId, actualRelease, "RELEASED", "Inventory released successfully");
    }



}

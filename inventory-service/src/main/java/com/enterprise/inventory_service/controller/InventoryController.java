package com.enterprise.inventory_service.controller;

import com.enterprise.inventory_service.dto.InventoryRequestDto;
import com.enterprise.inventory_service.dto.InventoryResponseDto;
import com.enterprise.inventory_service.dto.ReleaseStockResponseDto;
import com.enterprise.inventory_service.dto.ReserveInventoryRequestDto;
import com.enterprise.inventory_service.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    // CREATE INVENTORY

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InventoryResponseDto createInventory(
            @Valid
            @RequestBody
            InventoryRequestDto request
    ) {

        return inventoryService.createInventory(
                request
        );
    }


    // GET INVENTORY
    @GetMapping("/{productId}")
    public InventoryResponseDto getInventory(@PathVariable Long productId) {
        return inventoryService.getInventory(productId);
    }

    //update the stock
    @PutMapping("/upstock")
    public InventoryResponseDto updateStock(@Valid @RequestBody InventoryRequestDto requestDto){
        return  inventoryService.updateStock(requestDto);
    }



    // RESERVE INVENTORY
    @PostMapping("/reserve/{orderId}")
    public String reserveInventory(@PathVariable Long orderId,
            @Valid
            @RequestBody
            ReserveInventoryRequestDto request
    ) {

        inventoryService.reserveStock(
                orderId,
                request.getProductId(),
                request.getQuantity()
        );

        return "Inventory reserved successfully";
    }

    //release stock
    @PostMapping("/release")
    @ResponseStatus(HttpStatus.OK)
    public ReleaseStockResponseDto releaseStock(@RequestParam Long orderId,
                                                @RequestParam Long productId,
                                                @RequestParam Integer quantity){
        inventoryService.releaseStock(orderId, productId, quantity);

        return ReleaseStockResponseDto.builder()
                .message("Inventory released successfully")
                .orderId(orderId)
                .productId(productId)
                .quantity(quantity)
                .build();
    }
}

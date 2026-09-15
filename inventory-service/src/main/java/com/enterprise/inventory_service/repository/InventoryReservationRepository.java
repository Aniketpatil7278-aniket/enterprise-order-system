package com.enterprise.inventory_service.repository;

import com.enterprise.inventory_service.event.InventoryReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryReservationRepository extends JpaRepository<InventoryReservation, Long> {

    List<InventoryReservation> findByOrderIdAndReleasedFalse(Long orderId);

    boolean existsByOrderIdAndProductId(Long orderId, Long productId);
}
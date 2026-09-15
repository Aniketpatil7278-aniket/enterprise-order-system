package com.enterprise.inventory_service.repository;

import com.enterprise.inventory_service.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent , Long> {

    List<OutboxEvent> findTop50ByPublishedFalseOrderByCreatedAtAsc();
}

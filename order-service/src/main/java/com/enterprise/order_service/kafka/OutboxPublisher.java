//This component continuously checks the database for unpublished events.

package com.enterprise.order_service.kafka;


import com.enterprise.order_service.entity.OutboxEvent;
import com.enterprise.order_service.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j

public class OutboxPublisher {
    private final OutboxEventRepository outboxEventRepository;

    private final KafkaProducer kafkaProducer;

    @Scheduled(fixedDelay = 3000)
    public void publishEvents(){
        List<OutboxEvent> events = outboxEventRepository
                .findTop50ByPublishedFalseOrderByCreatedAtAsc();

        for (OutboxEvent event :events){
            try {
                kafkaProducer.send(
                        event.getEventType(),
                        event.getAggregateId(),
                        event.getPayload()
                );
                event.setPublished(true);
                event.setPublishedAt(LocalDateTime.now());
                outboxEventRepository.save(event);

                log.info("Outbox event published: {}", event.getId());

            }catch (Exception e){
                log.error("Failed to publish outbox event {}",event.getId() ,e);
            }
        }
    }
}


package com.sanedge.pointofsale.common.outbox;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

/**
 * Persistence for the shared {@code outbox} collection.
 *
 * <p>Producers call {@link #enqueue(Outbox)} inside their business write;
 * {@link OutboxPublisher} reads PENDING rows, publishes them to Kafka and marks
 * them PROCESSED — or increments {@code attempts} and parks the row as FAILED
 * once {@code max-attempts} is reached.
 */
@Repository
public interface OutboxRepository extends MongoRepository<Outbox, String> {

    /** Oldest-first batch of PENDING events for the publisher. */
    List<Outbox> findByStatusOrderByCreatedAtAsc(OutboxStatus status, Pageable pageable);

    long countByStatus(OutboxStatus status);

    /** Persists a new PENDING event; call inside the business transaction. */
    default void enqueue(Outbox event) {
        if (event.getStatus() == null) {
            event.setStatus(OutboxStatus.PENDING);
        }
        if (event.getAttempts() == null) {
            event.setAttempts(0);
        }
        if (event.getCreatedAt() == null) {
            event.setCreatedAt(LocalDateTime.now());
        }
        save(event);
    }

    @Query("{ '_id': ?0 }")
    @Update("{ '$set': { 'status': 'PROCESSED', 'processed_at': ?1, 'last_error': null } }")
    void markProcessed(String id, LocalDateTime processedAt);

    /**
     * Records a delivery failure: increments {@code attempts} and stores the
     * error. The row stays PENDING until {@code maxAttempts} is reached, at
     * which point it is parked as FAILED (dead letter).
     */
    default void markFailed(Outbox event, String error, int maxAttempts) {
        int attempts = event.getAttempts() == null ? 1 : event.getAttempts() + 1;
        event.setAttempts(attempts);
        event.setLastError(error);
        if (attempts >= maxAttempts) {
            event.setStatus(OutboxStatus.FAILED);
            event.setProcessedAt(LocalDateTime.now());
        }
        save(event);
    }
}

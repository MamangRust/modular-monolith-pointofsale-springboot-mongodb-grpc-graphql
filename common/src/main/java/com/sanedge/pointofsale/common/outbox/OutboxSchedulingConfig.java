package com.sanedge.pointofsale.common.outbox;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Enables the scheduler that drives {@link OutboxPublisher}.
 *
 * <p>Registered in {@code common} so every producer module (auth, merchant,
 * transaction, ...) gets the outbox relay without each application class having
 * to repeat {@code @EnableScheduling}. Gated on spring-kafka so consumer-only
 * modules are unaffected.
 */
@Configuration
@EnableScheduling
@ConditionalOnClass(KafkaTemplate.class)
public class OutboxSchedulingConfig {
}

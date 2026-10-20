package com.sanedge.pointofsale.common.outbox;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Guards the producer/consumer contract: the envelope emitted by
 * {@link OutboxEventFactory} must satisfy the fields that the email-service's
 * {@code EmailService.hasValidEnvelope} validates.
 */
class OutboxEventFactoryTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void emailEnvelopeCarriesEveryFieldTheConsumerRequires() throws Exception {
        OutboxEventFactory.Envelope envelope = OutboxEventFactory.email(
                "email-service-topic-auth-register", "user@example.com", "Welcome", "Hello there");

        assertThat(envelope.eventId()).isNotBlank();

        Map<String, Object> payload = mapper.readValue(envelope.payload(), new TypeReference<>() {
        });

        assertThat(payload.get("event_id")).isEqualTo(envelope.eventId());
        assertThat(payload.get("schema_version")).isEqualTo(OutboxEventFactory.SCHEMA_VERSION);
        assertThat(payload.get("event_type")).isEqualTo("email-service-topic-auth-register");
        assertThat(payload.get("email")).isEqualTo("user@example.com");
        assertThat(payload.get("subject")).isEqualTo("Welcome");
        assertThat(payload.get("body")).isEqualTo("Hello there");
        assertThat(payload.get("occurred_at")).isNotNull();
    }

    @Test
    void everyCallGetsAFreshEventId() {
        OutboxEventFactory.Envelope first = OutboxEventFactory.email("t", "a@b.c", "s", "b");
        OutboxEventFactory.Envelope second = OutboxEventFactory.email("t", "a@b.c", "s", "b");

        assertThat(first.eventId()).isNotEqualTo(second.eventId());
    }
}

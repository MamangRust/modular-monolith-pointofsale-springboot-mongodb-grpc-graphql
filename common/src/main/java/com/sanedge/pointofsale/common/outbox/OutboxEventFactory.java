package com.sanedge.pointofsale.common.outbox;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Builds the JSON envelope every email event must carry.
 *
 * <p>The consumer ({@code EmailService.hasValidEnvelope}) requires
 * {@code event_id}, {@code schema_version == 1}, {@code event_type},
 * {@code email}, {@code subject} and {@code body}; {@code occurred_at} is added
 * for observability. The same contract is used by every producer module so the
 * email-service can consume all topics through one code path.
 */
public final class OutboxEventFactory {

    /** Must match {@code EmailService.EVENT_SCHEMA_VERSION}. */
    public static final int SCHEMA_VERSION = 1;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private OutboxEventFactory() {
    }

    /**
     * Builds the email envelope for {@code topic}.
     *
     * @return the generated {@code event_id} plus the serialized JSON payload
     */
    public static Envelope email(String topic, String email, String subject, String body) {
        String eventId = UUID.randomUUID().toString();

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("email", email);
        payload.put("subject", subject);
        payload.put("body", body);
        payload.put("event_id", eventId);
        payload.put("schema_version", SCHEMA_VERSION);
        payload.put("event_type", topic);
        payload.put("occurred_at", Instant.now().toString());

        try {
            return new Envelope(eventId, MAPPER.writeValueAsString(payload));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Unable to serialize outbox payload for topic " + topic, e);
        }
    }

    /** The generated event id plus its serialized JSON payload. */
    public record Envelope(String eventId, String payload) {
    }
}

package com.sanedge.pointofsale.auth.service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Component;

/**
 * In-memory storage for email verification codes.
 *
 * <p>Two maps per registration, both 15-minute TTL:
 * {@code email → code} and {@code code → email}. Kept in-process because the
 * target project has no Redis dependency in the auth module; a single auth
 * replica is the expected deployment. Swap for a Redis-backed store if the auth
 * service is scaled out.
 */
@Component
public class VerificationCodeStore {

    /** Matches the previous 900 seconds. */
    static final Duration TTL = Duration.ofMinutes(15);

    private record Entry(String value, Instant expiresAt) {
        boolean expired() {
            return Instant.now().isAfter(expiresAt);
        }
    }

    private final ConcurrentMap<String, Entry> byEmail = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Entry> byCode = new ConcurrentHashMap<>();

    public void save(String email, String code) {
        Instant expiresAt = Instant.now().plus(TTL);
        byEmail.put(email, new Entry(code, expiresAt));
        byCode.put(code, new Entry(email, expiresAt));
    }

    /** Returns the email for {@code code}, or {@code null} when absent/expired. */
    public String resolveEmail(String code) {
        Entry entry = byCode.get(code);
        if (entry == null || entry.expired()) {
            byCode.remove(code);
            return null;
        }
        return entry.value();
    }

    public void consume(String email, String code) {
        byCode.remove(code);
        byEmail.remove(email);
    }
}

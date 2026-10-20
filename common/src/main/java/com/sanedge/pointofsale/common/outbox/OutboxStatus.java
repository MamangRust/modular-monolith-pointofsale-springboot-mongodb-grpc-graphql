package com.sanedge.pointofsale.common.outbox;

/**
 * Lifecycle of a row in the shared {@code outbox} collection.
 *
 * <p>There is no lease: the publisher increments {@code attempts} on failure
 * and, once {@code max-attempts} is reached, parks the row as {@link #FAILED}
 * (dead letter) instead of retrying forever.
 */
public enum OutboxStatus {
    PENDING,
    PROCESSED,
    FAILED
}

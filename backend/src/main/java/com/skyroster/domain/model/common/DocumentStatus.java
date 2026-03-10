package com.skyroster.domain.model.common;

/**
 * Status of a crew document's validity.
 * Transitions: VALID -> EXPIRING_SOON -> EXPIRED (managed by cron job).
 */
public enum DocumentStatus {
    VALID,
    EXPIRING_SOON,
    EXPIRED
}

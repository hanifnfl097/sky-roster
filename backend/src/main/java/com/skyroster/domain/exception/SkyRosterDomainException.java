package com.skyroster.domain.exception;

/**
 * Base exception for all SkyRoster domain-specific errors.
 * All business rule violations extend this class.
 */
public abstract class SkyRosterDomainException extends RuntimeException {

    private final String errorCode;

    protected SkyRosterDomainException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}

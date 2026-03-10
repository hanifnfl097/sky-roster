package com.skyroster.config;

import com.skyroster.domain.exception.SkyRosterDomainException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for consistent API error responses.
 * Never exposes stack traces to end users (security guardrail).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles all domain-specific exceptions (business rule violations).
     */
    @ExceptionHandler(SkyRosterDomainException.class)
    public ResponseEntity<Map<String, Object>> handleDomainException(SkyRosterDomainException ex) {
        HttpStatus status = resolveStatus(ex.getErrorCode());

        Map<String, Object> body = new HashMap<>();
        body.put("errorCode", ex.getErrorCode());
        body.put("message", ex.getMessage());
        body.put("timestamp", Instant.now().toString());

        return ResponseEntity.status(status).body(body);
    }

    /**
     * Handles bean validation failures on request DTOs.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        Map<String, Object> body = new HashMap<>();
        body.put("errorCode", "VALIDATION_FAILED");
        body.put("message", "Request validation failed");
        body.put("errors", fieldErrors);
        body.put("timestamp", Instant.now().toString());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Catch-all for unexpected exceptions.
     * Logs the full error internally but returns a safe message to the client.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        // Log internally for debugging (do not expose to client)
        Map<String, Object> body = new HashMap<>();
        body.put("errorCode", "INTERNAL_ERROR");
        body.put("message", "An unexpected error occurred. Please try again later.");
        body.put("timestamp", Instant.now().toString());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private HttpStatus resolveStatus(String errorCode) {
        return switch (errorCode) {
            case "RESOURCE_NOT_FOUND" -> HttpStatus.NOT_FOUND;
            case "DUPLICATE_RESOURCE" -> HttpStatus.CONFLICT;
            case "CREW_GROUNDED", "FTL_VIOLATION", "GEOFENCE_VIOLATION",
                    "CREW_COMPLEMENT_INCOMPLETE" ->
                HttpStatus.UNPROCESSABLE_ENTITY;
            default -> HttpStatus.BAD_REQUEST;
        };
    }
}

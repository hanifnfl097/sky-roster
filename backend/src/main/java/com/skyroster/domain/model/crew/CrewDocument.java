package com.skyroster.domain.model.crew;

import com.skyroster.domain.model.common.DocumentStatus;
import com.skyroster.domain.model.common.DocumentType;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Domain model for a crew member's document/certificate.
 * Tracks validity and expiry for regulatory compliance.
 */
public class CrewDocument {

    private UUID id;
    private UUID crewMemberId;
    private DocumentType documentType;
    private String documentNumber;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private DocumentStatus status;
    private String fileUrl;
    private Instant createdAt;
    private Instant updatedAt;

    /**
     * Checks if this document is expired as of the given date.
     *
     * @param referenceDate the date to check against
     * @return true if the document has expired
     */
    public boolean isExpiredAsOf(LocalDate referenceDate) {
        return expiryDate != null && !expiryDate.isAfter(referenceDate);
    }

    /**
     * Calculates the number of days until expiry from the given date.
     *
     * @param referenceDate the date to calculate from
     * @return days until expiry (negative if already expired)
     */
    public long daysUntilExpiry(LocalDate referenceDate) {
        if (expiryDate == null) {
            return Long.MAX_VALUE;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(referenceDate, expiryDate);
    }

    // --- Getters and Setters ---

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCrewMemberId() {
        return crewMemberId;
    }

    public void setCrewMemberId(UUID crewMemberId) {
        this.crewMemberId = crewMemberId;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public DocumentStatus getStatus() {
        return status;
    }

    public void setStatus(DocumentStatus status) {
        this.status = status;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}

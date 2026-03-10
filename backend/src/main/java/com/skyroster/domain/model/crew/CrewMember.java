package com.skyroster.domain.model.crew;

import com.skyroster.domain.model.common.CrewCategory;
import com.skyroster.domain.model.common.CrewRole;
import com.skyroster.domain.model.common.CrewStatus;
import com.skyroster.domain.model.common.DocumentStatus;
import com.skyroster.domain.model.common.DocumentType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Core domain model for a crew member.
 * Contains business logic for document validation and status management.
 * This class has ZERO framework dependencies (ADR-001 Rule #1).
 */
public class CrewMember {

    private UUID id;
    private UUID userId;
    private String staffId;
    private String firstName;
    private String lastName;
    private CrewRole crewRole;
    private CrewCategory crewCategory;
    private String baseStation;
    private CrewStatus status;
    private String groundingReason;
    private Instant groundedAt;
    private List<CrewDocument> documents;
    private Instant createdAt;
    private Instant updatedAt;

    public CrewMember() {
        this.documents = new ArrayList<>();
        this.status = CrewStatus.ACTIVE;
    }

    // --- Business Logic ---

    /**
     * Checks if this crew member has any expired mandatory documents.
     * Used by rostering validation (rule CR-001, CR-002).
     *
     * @return list of expired documents, empty if all are valid
     */
    public List<CrewDocument> getExpiredDocuments() {
        return documents.stream()
                .filter(doc -> doc.getStatus() == DocumentStatus.EXPIRED)
                .toList();
    }

    /**
     * Determines if this crew member is eligible for rostering.
     * Checks status and document currency.
     *
     * @return true if the crew member can be assigned to flights
     */
    public boolean isEligibleForRostering() {
        return status == CrewStatus.ACTIVE && getExpiredDocuments().isEmpty();
    }

    /**
     * Checks whether all mandatory documents are present and valid.
     * Common mandatory: MEDEX, TYPE_RATING_CERT, SEP (rule CR-001).
     * Flight Crew additional: ATPL/CPL, INSTRUMENT_RATING, SIM_CHECK (rule CR-002).
     *
     * @return true if all mandatory documents are valid
     */
    public boolean hasAllMandatoryDocuments() {
        boolean hasCommon = DocumentType.COMMON_MANDATORY.stream()
                .allMatch(this::hasValidDocument);

        if (crewCategory == CrewCategory.FLIGHT_CREW) {
            boolean hasLicense = DocumentType.FLIGHT_CREW_LICENSE.stream()
                    .anyMatch(this::hasValidDocument);
            boolean hasFlightCrewDocs = DocumentType.FLIGHT_CREW_MANDATORY.stream()
                    .allMatch(this::hasValidDocument);
            return hasCommon && hasLicense && hasFlightCrewDocs;
        }

        return hasCommon;
    }

    /**
     * Grounds this crew member with a documented reason.
     * Creates an immutable audit record via the grounding timestamp.
     *
     * @param reason the reason for grounding (e.g., "MEDEX expired")
     */
    public void ground(String reason) {
        this.status = CrewStatus.GROUNDED;
        this.groundingReason = reason;
        this.groundedAt = Instant.now();
    }

    /**
     * Activates a previously grounded crew member.
     * Should only be called after all documents are renewed and validated.
     */
    public void activate() {
        this.status = CrewStatus.ACTIVE;
        this.groundingReason = null;
        this.groundedAt = null;
    }

    // --- Private Helpers ---

    private boolean hasValidDocument(DocumentType type) {
        return documents.stream()
                .anyMatch(doc -> doc.getDocumentType() == type
                        && doc.getStatus() != DocumentStatus.EXPIRED);
    }

    // --- Getters and Setters ---

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public CrewRole getCrewRole() {
        return crewRole;
    }

    public void setCrewRole(CrewRole crewRole) {
        this.crewRole = crewRole;
        this.crewCategory = crewRole.getCategory();
    }

    public CrewCategory getCrewCategory() {
        return crewCategory;
    }

    public String getBaseStation() {
        return baseStation;
    }

    public void setBaseStation(String baseStation) {
        this.baseStation = baseStation;
    }

    public CrewStatus getStatus() {
        return status;
    }

    public String getGroundingReason() {
        return groundingReason;
    }

    public Instant getGroundedAt() {
        return groundedAt;
    }

    public List<CrewDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<CrewDocument> documents) {
        this.documents = documents;
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

package com.skyroster.domain.port.inbound;

import com.skyroster.domain.model.crew.CrewDocument;
import com.skyroster.domain.model.crew.CrewMember;

import java.util.List;
import java.util.UUID;

/**
 * Inbound port for crew management operations.
 * Driven by the web adapter (controllers).
 * Implements Clean Architecture: this interface lives in domain layer,
 * implemented by domain service, called by adapters.
 */
public interface CrewManagementUseCase {

    /**
     * Registers a new crew member in the system.
     *
     * @param crewMember the crew member to register
     * @return the created crew member with generated ID
     */
    CrewMember registerCrew(CrewMember crewMember);

    /**
     * Retrieves a crew member by their unique ID.
     *
     * @param id the crew member UUID
     * @return the crew member domain model
     */
    CrewMember getCrewById(UUID id);

    /**
     * Lists all crew members with optional filter criteria.
     *
     * @param role   optional role filter
     * @param status optional status filter
     * @param search optional search term (name or staff ID)
     * @param page   zero-based page number
     * @param size   page size
     * @return paginated list of crew members
     */
    List<CrewMember> listCrew(String role, String status, String search, int page, int size);

    /**
     * Updates an existing crew member's profile.
     *
     * @param id         the crew member UUID
     * @param crewMember the updated crew member data
     * @return the updated crew member
     */
    CrewMember updateCrew(UUID id, CrewMember crewMember);

    /**
     * Grounds a crew member (sets status to GROUNDED).
     * Hard-blocks the crew from all future rostering (rule CR-004).
     *
     * @param id     the crew member UUID
     * @param reason the documented reason for grounding
     */
    void groundCrew(UUID id, String reason);

    /**
     * Activates a previously grounded crew member.
     *
     * @param id the crew member UUID
     */
    void activateCrew(UUID id);

    /**
     * Adds a document (license, certificate) to a crew member.
     *
     * @param crewId   the crew member UUID
     * @param document the document to add
     * @return the created document
     */
    CrewDocument addDocument(UUID crewId, CrewDocument document);

    /**
     * Lists all documents for a crew member.
     *
     * @param crewId the crew member UUID
     * @return list of documents
     */
    List<CrewDocument> getDocuments(UUID crewId);

    /**
     * Lists documents expiring within the specified number of days.
     *
     * @param days number of days threshold
     * @return list of expiring documents across all crew
     */
    List<CrewDocument> getExpiringDocuments(int days);
}

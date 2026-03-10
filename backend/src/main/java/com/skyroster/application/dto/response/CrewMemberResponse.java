package com.skyroster.application.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for crew member data.
 * Shapes the API output without exposing domain internals.
 */
@Data
@Builder
public class CrewMemberResponse {

    private UUID id;
    private String staffId;
    private String firstName;
    private String lastName;
    private String fullName;
    private String crewRole;
    private String crewCategory;
    private String baseStation;
    private String status;
    private String groundingReason;
    private Instant groundedAt;
    private List<CrewDocumentResponse> documents;
    private Instant createdAt;
    private Instant updatedAt;
}

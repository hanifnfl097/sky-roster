package com.skyroster.application.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Response DTO for crew document data.
 */
@Data
@Builder
public class CrewDocumentResponse {

    private UUID id;
    private UUID crewMemberId;
    private String documentType;
    private String documentNumber;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String status;
    private String fileUrl;
    private long daysUntilExpiry;
}

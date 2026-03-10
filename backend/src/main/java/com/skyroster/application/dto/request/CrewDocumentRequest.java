package com.skyroster.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

/**
 * Request DTO for adding or updating a crew document.
 */
@Data
public class CrewDocumentRequest {

    @NotBlank(message = "Document type is required")
    @Pattern(regexp = "MEDEX|ATPL|CPL|INSTRUMENT_RATING|SEP|SIM_CHECK|TYPE_RATING_CERT", message = "Invalid document type")
    private String documentType;

    private String documentNumber;

    @NotNull(message = "Issue date is required")
    private LocalDate issueDate;

    @NotNull(message = "Expiry date is required")
    private LocalDate expiryDate;

    private String fileUrl;
}

package com.skyroster.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request DTO for creating or updating a crew member.
 * Input validation happens at the controller boundary (fail-fast principle).
 */
@Data
public class CrewMemberRequest {

    @NotBlank(message = "Staff ID is required")
    @Size(max = 20, message = "Staff ID must not exceed 20 characters")
    private String staffId;

    @NotBlank(message = "Email is required")
    @jakarta.validation.constraints.Email(message = "Email must be a valid format")
    private String email;

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;

    @NotBlank(message = "Crew role is required")
    @Pattern(regexp = "CAPTAIN|FIRST_OFFICER|PURSER|FLIGHT_ATTENDANT", message = "Crew role must be CAPTAIN, FIRST_OFFICER, PURSER, or FLIGHT_ATTENDANT")
    private String crewRole;

    @NotBlank(message = "Base station is required")
    @Size(max = 5, message = "Base station must be an IATA code (max 5 chars)")
    private String baseStation;
}

package com.skyroster.adapter.inbound.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyroster.application.dto.request.CrewMemberRequest;
import com.skyroster.config.GlobalExceptionHandler;
import com.skyroster.domain.model.common.CrewCategory;
import com.skyroster.domain.model.common.CrewRole;
import com.skyroster.domain.model.common.CrewStatus;
import com.skyroster.domain.model.crew.CrewMember;
import com.skyroster.domain.port.inbound.CrewManagementUseCase;
import com.skyroster.domain.service.CrewManagementService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller tests using MockMvc with a sliced context.
 * Tests HTTP contract: status codes, JSON structure, validation errors.
 */
@WebMvcTest(controllers = { CrewController.class, GlobalExceptionHandler.class })
@org.springframework.context.annotation.Import(CrewControllerTest.TestSecurityConfig.class)
class CrewControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CrewManagementUseCase crewManagementUseCase;

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
            http.csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }
    }

    private CrewMember sampleCrew;
    private UUID sampleId;

    @BeforeEach
    void setUp() {
        org.mockito.Mockito.reset(crewManagementUseCase);

        sampleId = UUID.randomUUID();
        sampleCrew = new CrewMember();
        sampleCrew.setId(sampleId);
        sampleCrew.setUserId(UUID.randomUUID());
        sampleCrew.setStaffId("SKY-001");
        sampleCrew.setFirstName("John");
        sampleCrew.setLastName("Doe");
        sampleCrew.setCrewRole(CrewRole.CAPTAIN);
        sampleCrew.setBaseStation("CGK");
        sampleCrew.setDocuments(new ArrayList<>());
        sampleCrew.setCreatedAt(Instant.now());
        sampleCrew.setUpdatedAt(Instant.now());
    }

    @Nested
    @DisplayName("POST /crew")
    class RegisterCrew {

        @Test
        @DisplayName("should return 201 with created crew on valid request")
        void shouldReturn201OnValidRequest() throws Exception {
            CrewMemberRequest request = new CrewMemberRequest();
            request.setStaffId("SKY-002");
            request.setEmail("jane@skyroster.local");
            request.setFirstName("Jane");
            request.setLastName("Smith");
            request.setCrewRole("PURSER");
            request.setBaseStation("DPS");

            when(crewManagementUseCase.registerCrew(any(CrewMember.class)))
                    .thenReturn(sampleCrew);

            mockMvc.perform(post("/crew")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.staffId").value("SKY-001"))
                    .andExpect(jsonPath("$.firstName").value("John"))
                    .andExpect(jsonPath("$.crewRole").value("CAPTAIN"))
                    .andExpect(jsonPath("$.status").value("ACTIVE"));
        }

        @Test
        @DisplayName("should return 400 when staff ID is blank")
        void shouldReturn400WhenStaffIdBlank() throws Exception {
            CrewMemberRequest request = new CrewMemberRequest();
            request.setStaffId("");
            request.setEmail("test@test.com");
            request.setFirstName("Test");
            request.setLastName("User");
            request.setCrewRole("CAPTAIN");
            request.setBaseStation("CGK");

            mockMvc.perform(post("/crew")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"))
                    .andExpect(jsonPath("$.errors.staffId").exists());
        }

        @Test
        @DisplayName("should return 400 when crew role is invalid")
        void shouldReturn400WhenCrewRoleInvalid() throws Exception {
            CrewMemberRequest request = new CrewMemberRequest();
            request.setStaffId("SKY-003");
            request.setEmail("test@test.com");
            request.setFirstName("Test");
            request.setLastName("User");
            request.setCrewRole("PILOT");
            request.setBaseStation("CGK");

            mockMvc.perform(post("/crew")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.crewRole").exists());
        }

        @Test
        @DisplayName("should return 409 when staff ID already exists")
        void shouldReturn409WhenDuplicate() throws Exception {
            CrewMemberRequest request = new CrewMemberRequest();
            request.setStaffId("SKY-001");
            request.setEmail("dup@test.com");
            request.setFirstName("Dup");
            request.setLastName("User");
            request.setCrewRole("CAPTAIN");
            request.setBaseStation("CGK");

            when(crewManagementUseCase.registerCrew(any()))
                    .thenThrow(new CrewManagementService.DuplicateResourceException("staffId", "SKY-001"));

            mockMvc.perform(post("/crew")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.errorCode").value("DUPLICATE_RESOURCE"));
        }
    }

    @Nested
    @DisplayName("GET /crew")
    class ListCrew {

        @Test
        @DisplayName("should return 200 with list of crew members")
        void shouldReturn200WithList() throws Exception {
            when(crewManagementUseCase.listCrew(isNull(), isNull(), isNull(), eq(0), eq(20)))
                    .thenReturn(List.of(sampleCrew));

            mockMvc.perform(get("/crew")
                    .param("page", "0")
                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].staffId").value("SKY-001"))
                    .andExpect(jsonPath("$[0].crewCategory").value("FLIGHT_CREW"));
        }
    }

    @Nested
    @DisplayName("GET /crew/{id}")
    class GetCrewById {

        @Test
        @DisplayName("should return 200 with crew detail")
        void shouldReturn200WithDetail() throws Exception {
            when(crewManagementUseCase.getCrewById(sampleId)).thenReturn(sampleCrew);

            mockMvc.perform(get("/crew/{id}", sampleId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.staffId").value("SKY-001"))
                    .andExpect(jsonPath("$.fullName").value("John Doe"));
        }

        @Test
        @DisplayName("should return 404 when crew not found")
        void shouldReturn404WhenNotFound() throws Exception {
            UUID missingId = UUID.randomUUID();
            when(crewManagementUseCase.getCrewById(missingId))
                    .thenThrow(new CrewManagementService.ResourceNotFoundException(missingId, "CrewMember"));

            mockMvc.perform(get("/crew/{id}", missingId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value("RESOURCE_NOT_FOUND"));
        }
    }

    @Nested
    @DisplayName("PATCH /crew/{id}/status")
    class ChangeStatus {

        @Test
        @DisplayName("should return 204 when grounding crew")
        void shouldReturn204WhenGrounding() throws Exception {
            when(crewManagementUseCase.getCrewById(sampleId)).thenReturn(sampleCrew);

            mockMvc.perform(patch("/crew/{id}/status", sampleId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"action\":\"GROUND\",\"reason\":\"MEDEX expired\"}"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("should return 400 when action is invalid")
        void shouldReturn400WhenActionInvalid() throws Exception {
            mockMvc.perform(patch("/crew/{id}/status", sampleId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"action\":\"INVALID\"}"))
                    .andExpect(status().isBadRequest());
        }
    }
}

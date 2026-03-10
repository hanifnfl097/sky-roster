package com.skyroster.domain.service;

import com.skyroster.adapter.outbound.persistence.entity.AuditLogEntity;
import com.skyroster.adapter.outbound.persistence.entity.CrewDocumentEntity;
import com.skyroster.adapter.outbound.persistence.entity.CrewMemberEntity;
import com.skyroster.adapter.outbound.persistence.entity.UserEntity;
import com.skyroster.adapter.outbound.persistence.repository.JpaAuditLogRepository;
import com.skyroster.adapter.outbound.persistence.repository.JpaCrewDocumentRepository;
import com.skyroster.adapter.outbound.persistence.repository.JpaCrewMemberRepository;
import com.skyroster.adapter.outbound.persistence.repository.JpaUserRepository;
import com.skyroster.domain.model.common.CrewRole;
import com.skyroster.domain.model.common.DocumentType;
import com.skyroster.domain.model.crew.CrewDocument;
import com.skyroster.domain.model.crew.CrewMember;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CrewManagementService.
 * Uses Mockito to isolate the service from persistence.
 */
@ExtendWith(MockitoExtension.class)
class CrewManagementServiceTest {

    @Mock
    private JpaCrewMemberRepository crewMemberRepository;
    @Mock
    private JpaCrewDocumentRepository crewDocumentRepository;
    @Mock
    private JpaUserRepository userRepository;
    @Mock
    private JpaAuditLogRepository auditLogRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CrewManagementService service;

    private CrewMemberEntity sampleEntity;
    private UUID sampleId;

    @BeforeEach
    void setUp() {
        sampleId = UUID.randomUUID();
        sampleEntity = CrewMemberEntity.builder()
                .id(sampleId)
                .userId(UUID.randomUUID())
                .staffId("SKY-001")
                .firstName("John")
                .lastName("Doe")
                .crewRole("CAPTAIN")
                .crewCategory("FLIGHT_CREW")
                .baseStation("CGK")
                .status("ACTIVE")
                .documents(new ArrayList<>())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Nested
    @DisplayName("registerCrew")
    class RegisterCrew {

        @Test
        @DisplayName("should register crew and create user account")
        void shouldRegisterCrewAndCreateUser() {
            CrewMember input = new CrewMember();
            input.setStaffId("SKY-002");
            input.setFirstName("Jane");
            input.setLastName("Smith");
            input.setCrewRole(CrewRole.PURSER);
            input.setBaseStation("DPS");

            when(crewMemberRepository.existsByStaffId("SKY-002")).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("hashed");
            when(userRepository.save(any(UserEntity.class)))
                    .thenAnswer(inv -> {
                        UserEntity u = inv.getArgument(0);
                        u.setId(UUID.randomUUID());
                        return u;
                    });
            when(crewMemberRepository.save(any(CrewMemberEntity.class)))
                    .thenAnswer(inv -> {
                        CrewMemberEntity e = inv.getArgument(0);
                        e.setId(UUID.randomUUID());
                        e.setDocuments(new ArrayList<>());
                        e.setCreatedAt(Instant.now());
                        e.setUpdatedAt(Instant.now());
                        return e;
                    });
            when(auditLogRepository.save(any(AuditLogEntity.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            CrewMember result = service.registerCrew(input);

            assertNotNull(result);
            assertEquals("Jane", result.getFirstName());
            verify(userRepository).save(any(UserEntity.class));
            verify(crewMemberRepository).save(any(CrewMemberEntity.class));
            verify(auditLogRepository).save(any(AuditLogEntity.class));
        }

        @Test
        @DisplayName("should throw when staff ID already exists")
        void shouldThrowWhenStaffIdDuplicate() {
            CrewMember input = new CrewMember();
            input.setStaffId("SKY-001");

            when(crewMemberRepository.existsByStaffId("SKY-001")).thenReturn(true);

            assertThrows(CrewManagementService.DuplicateResourceException.class,
                    () -> service.registerCrew(input));

            verify(crewMemberRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("getCrewById")
    class GetCrewById {

        @Test
        @DisplayName("should return crew member when found")
        void shouldReturnCrewWhenFound() {
            when(crewMemberRepository.findById(sampleId)).thenReturn(Optional.of(sampleEntity));

            CrewMember result = service.getCrewById(sampleId);

            assertNotNull(result);
            assertEquals("SKY-001", result.getStaffId());
            assertEquals("John", result.getFirstName());
        }

        @Test
        @DisplayName("should throw when crew member not found")
        void shouldThrowWhenNotFound() {
            UUID missingId = UUID.randomUUID();
            when(crewMemberRepository.findById(missingId)).thenReturn(Optional.empty());

            assertThrows(CrewManagementService.ResourceNotFoundException.class,
                    () -> service.getCrewById(missingId));
        }
    }

    @Nested
    @DisplayName("listCrew")
    class ListCrew {

        @Test
        @DisplayName("should return paginated list with filters applied")
        void shouldReturnPaginatedListWithFilters() {
            Page<CrewMemberEntity> page = new PageImpl<>(List.of(sampleEntity));
            when(crewMemberRepository.findAllWithFilters(
                    eq("CAPTAIN"), isNull(), isNull(), any(Pageable.class)))
                    .thenReturn(page);

            List<CrewMember> result = service.listCrew("CAPTAIN", null, null, 0, 20);

            assertEquals(1, result.size());
            assertEquals("SKY-001", result.get(0).getStaffId());
        }
    }

    @Nested
    @DisplayName("groundCrew (CR-003, CR-006)")
    class GroundCrew {

        @Test
        @DisplayName("should ground crew and create audit log")
        void shouldGroundCrewAndCreateAuditLog() {
            when(crewMemberRepository.findById(sampleId)).thenReturn(Optional.of(sampleEntity));
            when(crewMemberRepository.save(any())).thenReturn(sampleEntity);
            when(auditLogRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            service.groundCrew(sampleId, "MEDEX expired");

            assertEquals("GROUNDED", sampleEntity.getStatus());
            assertEquals("MEDEX expired", sampleEntity.getGroundingReason());
            assertNotNull(sampleEntity.getGroundedAt());

            ArgumentCaptor<AuditLogEntity> auditCaptor = ArgumentCaptor.forClass(AuditLogEntity.class);
            verify(auditLogRepository).save(auditCaptor.capture());
            assertEquals("GROUNDED", auditCaptor.getValue().getAction());
            assertEquals(sampleId, auditCaptor.getValue().getEntityId());
        }
    }

    @Nested
    @DisplayName("activateCrew")
    class ActivateCrew {

        @Test
        @DisplayName("should activate grounded crew and clear grounding data")
        void shouldActivateAndClearGrounding() {
            sampleEntity.setStatus("GROUNDED");
            sampleEntity.setGroundingReason("MEDEX expired");
            sampleEntity.setGroundedAt(Instant.now());

            when(crewMemberRepository.findById(sampleId)).thenReturn(Optional.of(sampleEntity));
            when(crewMemberRepository.save(any())).thenReturn(sampleEntity);
            when(auditLogRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            service.activateCrew(sampleId);

            assertEquals("ACTIVE", sampleEntity.getStatus());
            assertNull(sampleEntity.getGroundingReason());
            assertNull(sampleEntity.getGroundedAt());
        }
    }

    @Nested
    @DisplayName("addDocument")
    class AddDocument {

        @Test
        @DisplayName("should add a valid document to crew member")
        void shouldAddValidDocument() {
            when(crewMemberRepository.findById(sampleId)).thenReturn(Optional.of(sampleEntity));
            when(crewDocumentRepository.save(any(CrewDocumentEntity.class)))
                    .thenAnswer(inv -> {
                        CrewDocumentEntity e = inv.getArgument(0);
                        e.setId(UUID.randomUUID());
                        e.setCreatedAt(Instant.now());
                        e.setUpdatedAt(Instant.now());
                        return e;
                    });
            when(auditLogRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            CrewDocument input = new CrewDocument();
            input.setDocumentType(DocumentType.MEDEX);
            input.setIssueDate(LocalDate.now().minusMonths(6));
            input.setExpiryDate(LocalDate.now().plusMonths(6));

            CrewDocument result = service.addDocument(sampleId, input);

            assertNotNull(result.getId());
            assertEquals(DocumentType.MEDEX, result.getDocumentType());
            verify(crewDocumentRepository).save(any());
        }

        @Test
        @DisplayName("should set status to EXPIRED if document is already expired")
        void shouldSetExpiredStatusForExpiredDocument() {
            when(crewMemberRepository.findById(sampleId)).thenReturn(Optional.of(sampleEntity));
            when(crewDocumentRepository.save(any(CrewDocumentEntity.class)))
                    .thenAnswer(inv -> {
                        CrewDocumentEntity e = inv.getArgument(0);
                        e.setId(UUID.randomUUID());
                        e.setCreatedAt(Instant.now());
                        e.setUpdatedAt(Instant.now());
                        return e;
                    });
            when(auditLogRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            CrewDocument input = new CrewDocument();
            input.setDocumentType(DocumentType.SEP);
            input.setIssueDate(LocalDate.now().minusYears(2));
            input.setExpiryDate(LocalDate.now().minusDays(1));

            service.addDocument(sampleId, input);

            ArgumentCaptor<CrewDocumentEntity> captor = ArgumentCaptor.forClass(CrewDocumentEntity.class);
            verify(crewDocumentRepository).save(captor.capture());
            assertEquals("EXPIRED", captor.getValue().getStatus());
        }
    }

    @Nested
    @DisplayName("getExpiringDocuments")
    class GetExpiringDocuments {

        @Test
        @DisplayName("should return documents expiring within threshold")
        void shouldReturnExpiringDocuments() {
            CrewDocumentEntity docEntity = CrewDocumentEntity.builder()
                    .id(UUID.randomUUID())
                    .crewMember(sampleEntity)
                    .documentType("MEDEX")
                    .issueDate(LocalDate.now().minusYears(1))
                    .expiryDate(LocalDate.now().plusDays(10))
                    .status("VALID")
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();

            when(crewDocumentRepository.findExpiringBefore(any(LocalDate.class)))
                    .thenReturn(List.of(docEntity));

            List<CrewDocument> result = service.getExpiringDocuments(30);

            assertEquals(1, result.size());
            assertEquals(DocumentType.MEDEX, result.get(0).getDocumentType());
        }
    }
}

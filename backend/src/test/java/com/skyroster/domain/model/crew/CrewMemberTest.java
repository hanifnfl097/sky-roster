package com.skyroster.domain.model.crew;

import com.skyroster.domain.model.common.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CrewMember domain model business logic.
 * Tests rules CR-001, CR-002, CR-004.
 */
class CrewMemberTest {

    private CrewMember crewMember;

    @BeforeEach
    void setUp() {
        crewMember = new CrewMember();
        crewMember.setStaffId("SKY-001");
        crewMember.setFirstName("John");
        crewMember.setLastName("Doe");
        crewMember.setCrewRole(CrewRole.CAPTAIN);
        crewMember.setBaseStation("CGK");
    }

    @Nested
    @DisplayName("Role and Category")
    class RoleAndCategory {

        @Test
        @DisplayName("should classify CAPTAIN as FLIGHT_CREW")
        void shouldClassifyCaptainAsFlightCrew() {
            crewMember.setCrewRole(CrewRole.CAPTAIN);
            assertEquals(CrewCategory.FLIGHT_CREW, crewMember.getCrewCategory());
        }

        @Test
        @DisplayName("should classify FIRST_OFFICER as FLIGHT_CREW")
        void shouldClassifyFirstOfficerAsFlightCrew() {
            crewMember.setCrewRole(CrewRole.FIRST_OFFICER);
            assertEquals(CrewCategory.FLIGHT_CREW, crewMember.getCrewCategory());
        }

        @Test
        @DisplayName("should classify PURSER as CABIN_CREW")
        void shouldClassifyPurserAsCabinCrew() {
            crewMember.setCrewRole(CrewRole.PURSER);
            assertEquals(CrewCategory.CABIN_CREW, crewMember.getCrewCategory());
        }

        @Test
        @DisplayName("should classify FLIGHT_ATTENDANT as CABIN_CREW")
        void shouldClassifyFlightAttendantAsCabinCrew() {
            crewMember.setCrewRole(CrewRole.FLIGHT_ATTENDANT);
            assertEquals(CrewCategory.CABIN_CREW, crewMember.getCrewCategory());
        }
    }

    @Nested
    @DisplayName("Full Name")
    class FullName {

        @Test
        @DisplayName("should return concatenated first and last name")
        void shouldReturnFullName() {
            assertEquals("John Doe", crewMember.getFullName());
        }
    }

    @Nested
    @DisplayName("Document Expiry (CR-001, CR-002)")
    class DocumentExpiry {

        @Test
        @DisplayName("should return empty list when no documents are expired")
        void shouldReturnEmptyWhenNoExpiredDocs() {
            CrewDocument validDoc = createDocument(DocumentType.MEDEX, DocumentStatus.VALID);
            crewMember.setDocuments(List.of(validDoc));

            assertTrue(crewMember.getExpiredDocuments().isEmpty());
        }

        @Test
        @DisplayName("should return expired documents")
        void shouldReturnExpiredDocuments() {
            CrewDocument validDoc = createDocument(DocumentType.MEDEX, DocumentStatus.VALID);
            CrewDocument expiredDoc = createDocument(DocumentType.SEP, DocumentStatus.EXPIRED);
            crewMember.setDocuments(List.of(validDoc, expiredDoc));

            List<CrewDocument> expired = crewMember.getExpiredDocuments();
            assertEquals(1, expired.size());
            assertEquals(DocumentType.SEP, expired.get(0).getDocumentType());
        }
    }

    @Nested
    @DisplayName("Rostering Eligibility (CR-004)")
    class RosteringEligibility {

        @Test
        @DisplayName("should be eligible when ACTIVE and no expired documents")
        void shouldBeEligibleWhenActiveAndDocsValid() {
            crewMember.setDocuments(List.of(
                    createDocument(DocumentType.MEDEX, DocumentStatus.VALID)));

            assertTrue(crewMember.isEligibleForRostering());
        }

        @Test
        @DisplayName("should NOT be eligible when GROUNDED")
        void shouldNotBeEligibleWhenGrounded() {
            crewMember.ground("Expired MEDEX");

            assertFalse(crewMember.isEligibleForRostering());
        }

        @Test
        @DisplayName("should NOT be eligible when documents are expired")
        void shouldNotBeEligibleWhenDocsExpired() {
            crewMember.setDocuments(List.of(
                    createDocument(DocumentType.MEDEX, DocumentStatus.EXPIRED)));

            assertFalse(crewMember.isEligibleForRostering());
        }
    }

    @Nested
    @DisplayName("Mandatory Documents Check (CR-001, CR-002)")
    class MandatoryDocuments {

        @Test
        @DisplayName("should pass for cabin crew with MEDEX, TYPE_RATING_CERT, SEP")
        void shouldPassForCabinCrewWithCommonDocs() {
            crewMember.setCrewRole(CrewRole.FLIGHT_ATTENDANT);
            crewMember.setDocuments(List.of(
                    createDocument(DocumentType.MEDEX, DocumentStatus.VALID),
                    createDocument(DocumentType.TYPE_RATING_CERT, DocumentStatus.VALID),
                    createDocument(DocumentType.SEP, DocumentStatus.VALID)));

            assertTrue(crewMember.hasAllMandatoryDocuments());
        }

        @Test
        @DisplayName("should fail for cabin crew missing SEP")
        void shouldFailForCabinCrewMissingSep() {
            crewMember.setCrewRole(CrewRole.FLIGHT_ATTENDANT);
            crewMember.setDocuments(List.of(
                    createDocument(DocumentType.MEDEX, DocumentStatus.VALID),
                    createDocument(DocumentType.TYPE_RATING_CERT, DocumentStatus.VALID)));

            assertFalse(crewMember.hasAllMandatoryDocuments());
        }

        @Test
        @DisplayName("should pass for flight crew with all required documents")
        void shouldPassForFlightCrewWithAllDocs() {
            crewMember.setCrewRole(CrewRole.CAPTAIN);
            crewMember.setDocuments(List.of(
                    createDocument(DocumentType.MEDEX, DocumentStatus.VALID),
                    createDocument(DocumentType.TYPE_RATING_CERT, DocumentStatus.VALID),
                    createDocument(DocumentType.SEP, DocumentStatus.VALID),
                    createDocument(DocumentType.ATPL, DocumentStatus.VALID),
                    createDocument(DocumentType.INSTRUMENT_RATING, DocumentStatus.VALID),
                    createDocument(DocumentType.SIM_CHECK, DocumentStatus.VALID)));

            assertTrue(crewMember.hasAllMandatoryDocuments());
        }

        @Test
        @DisplayName("should fail for flight crew missing license (ATPL/CPL)")
        void shouldFailForFlightCrewMissingLicense() {
            crewMember.setCrewRole(CrewRole.CAPTAIN);
            crewMember.setDocuments(List.of(
                    createDocument(DocumentType.MEDEX, DocumentStatus.VALID),
                    createDocument(DocumentType.TYPE_RATING_CERT, DocumentStatus.VALID),
                    createDocument(DocumentType.SEP, DocumentStatus.VALID),
                    createDocument(DocumentType.INSTRUMENT_RATING, DocumentStatus.VALID),
                    createDocument(DocumentType.SIM_CHECK, DocumentStatus.VALID)));

            assertFalse(crewMember.hasAllMandatoryDocuments());
        }

        @Test
        @DisplayName("should accept CPL as alternative to ATPL for flight crew")
        void shouldAcceptCplAsAlternativeToAtpl() {
            crewMember.setCrewRole(CrewRole.FIRST_OFFICER);
            crewMember.setDocuments(List.of(
                    createDocument(DocumentType.MEDEX, DocumentStatus.VALID),
                    createDocument(DocumentType.TYPE_RATING_CERT, DocumentStatus.VALID),
                    createDocument(DocumentType.SEP, DocumentStatus.VALID),
                    createDocument(DocumentType.CPL, DocumentStatus.VALID),
                    createDocument(DocumentType.INSTRUMENT_RATING, DocumentStatus.VALID),
                    createDocument(DocumentType.SIM_CHECK, DocumentStatus.VALID)));

            assertTrue(crewMember.hasAllMandatoryDocuments());
        }
    }

    @Nested
    @DisplayName("Grounding / Activation")
    class GroundingActivation {

        @Test
        @DisplayName("should set status to GROUNDED with reason and timestamp")
        void shouldGroundWithReasonAndTimestamp() {
            crewMember.ground("MEDEX expired");

            assertEquals(CrewStatus.GROUNDED, crewMember.getStatus());
            assertEquals("MEDEX expired", crewMember.getGroundingReason());
            assertNotNull(crewMember.getGroundedAt());
        }

        @Test
        @DisplayName("should clear grounding data on activation")
        void shouldClearGroundingOnActivation() {
            crewMember.ground("MEDEX expired");
            crewMember.activate();

            assertEquals(CrewStatus.ACTIVE, crewMember.getStatus());
            assertNull(crewMember.getGroundingReason());
            assertNull(crewMember.getGroundedAt());
        }
    }

    // --- Helpers ---

    private CrewDocument createDocument(DocumentType type, DocumentStatus status) {
        CrewDocument doc = new CrewDocument();
        doc.setDocumentType(type);
        doc.setStatus(status);
        doc.setIssueDate(LocalDate.now().minusYears(1));
        doc.setExpiryDate(status == DocumentStatus.EXPIRED
                ? LocalDate.now().minusDays(1)
                : LocalDate.now().plusYears(1));
        return doc;
    }
}

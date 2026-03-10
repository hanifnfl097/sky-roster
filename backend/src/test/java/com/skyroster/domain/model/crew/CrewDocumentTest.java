package com.skyroster.domain.model.crew;

import com.skyroster.domain.model.common.DocumentStatus;
import com.skyroster.domain.model.common.DocumentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CrewDocument domain model.
 */
class CrewDocumentTest {

    @Test
    @DisplayName("should detect expired document when expiry date is in the past")
    void shouldDetectExpiredDocument() {
        CrewDocument doc = createDocumentWithExpiry(LocalDate.now().minusDays(1));

        assertTrue(doc.isExpiredAsOf(LocalDate.now()));
    }

    @Test
    @DisplayName("should NOT detect expired when expiry date is today")
    void shouldNotDetectExpiredWhenExpiryIsToday() {
        CrewDocument doc = createDocumentWithExpiry(LocalDate.now());

        assertTrue(doc.isExpiredAsOf(LocalDate.now()));
    }

    @Test
    @DisplayName("should NOT detect expired when expiry date is in the future")
    void shouldNotDetectExpiredWhenExpiryInFuture() {
        CrewDocument doc = createDocumentWithExpiry(LocalDate.now().plusDays(30));

        assertFalse(doc.isExpiredAsOf(LocalDate.now()));
    }

    @Test
    @DisplayName("should calculate correct days until expiry for future date")
    void shouldCalculateDaysUntilExpiryForFutureDate() {
        LocalDate expiry = LocalDate.now().plusDays(15);
        CrewDocument doc = createDocumentWithExpiry(expiry);

        assertEquals(15, doc.daysUntilExpiry(LocalDate.now()));
    }

    @Test
    @DisplayName("should return negative days for already expired document")
    void shouldReturnNegativeDaysForExpiredDocument() {
        LocalDate expiry = LocalDate.now().minusDays(5);
        CrewDocument doc = createDocumentWithExpiry(expiry);

        assertEquals(-5, doc.daysUntilExpiry(LocalDate.now()));
    }

    @Test
    @DisplayName("should return zero days when expiry is today")
    void shouldReturnZeroDaysWhenExpiryIsToday() {
        CrewDocument doc = createDocumentWithExpiry(LocalDate.now());

        assertEquals(0, doc.daysUntilExpiry(LocalDate.now()));
    }

    @Test
    @DisplayName("should return MAX_VALUE when expiry date is null")
    void shouldReturnMaxValueWhenExpiryIsNull() {
        CrewDocument doc = new CrewDocument();
        doc.setExpiryDate(null);

        assertEquals(Long.MAX_VALUE, doc.daysUntilExpiry(LocalDate.now()));
    }

    private CrewDocument createDocumentWithExpiry(LocalDate expiryDate) {
        CrewDocument doc = new CrewDocument();
        doc.setDocumentType(DocumentType.MEDEX);
        doc.setStatus(DocumentStatus.VALID);
        doc.setIssueDate(LocalDate.now().minusYears(1));
        doc.setExpiryDate(expiryDate);
        return doc;
    }
}

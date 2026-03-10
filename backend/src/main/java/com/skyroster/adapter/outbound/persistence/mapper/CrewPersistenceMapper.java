package com.skyroster.adapter.outbound.persistence.mapper;

import com.skyroster.adapter.outbound.persistence.entity.CrewDocumentEntity;
import com.skyroster.adapter.outbound.persistence.entity.CrewMemberEntity;
import com.skyroster.domain.model.common.*;
import com.skyroster.domain.model.crew.CrewDocument;
import com.skyroster.domain.model.crew.CrewMember;

import java.util.List;

/**
 * Maps between JPA entities and domain models.
 * Per ADR-001: JPA entities are NOT domain models.
 * This mapper bridges the adapter and domain layers.
 */
public final class CrewPersistenceMapper {

    private CrewPersistenceMapper() {
        // Utility class — prevent instantiation
    }

    // --- CrewMember ---

    public static CrewMember toDomain(CrewMemberEntity entity) {
        if (entity == null)
            return null;

        CrewMember crew = new CrewMember();
        crew.setId(entity.getId());
        crew.setUserId(entity.getUserId());
        crew.setStaffId(entity.getStaffId());
        crew.setFirstName(entity.getFirstName());
        crew.setLastName(entity.getLastName());
        crew.setCrewRole(CrewRole.valueOf(entity.getCrewRole()));
        crew.setBaseStation(entity.getBaseStation());
        crew.setCreatedAt(entity.getCreatedAt());
        crew.setUpdatedAt(entity.getUpdatedAt());

        if (entity.getDocuments() != null) {
            crew.setDocuments(entity.getDocuments().stream()
                    .map(CrewPersistenceMapper::toDomain)
                    .toList());
        }

        return crew;
    }

    public static CrewMemberEntity toEntity(CrewMember domain) {
        if (domain == null)
            return null;

        return CrewMemberEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .staffId(domain.getStaffId())
                .firstName(domain.getFirstName())
                .lastName(domain.getLastName())
                .crewRole(domain.getCrewRole().name())
                .crewCategory(domain.getCrewCategory().name())
                .baseStation(domain.getBaseStation())
                .status(domain.getStatus().name())
                .groundingReason(domain.getGroundingReason())
                .groundedAt(domain.getGroundedAt())
                .build();
    }

    public static void updateEntity(CrewMemberEntity entity, CrewMember domain) {
        entity.setFirstName(domain.getFirstName());
        entity.setLastName(domain.getLastName());
        entity.setCrewRole(domain.getCrewRole().name());
        entity.setCrewCategory(domain.getCrewCategory().name());
        entity.setBaseStation(domain.getBaseStation());
    }

    // --- CrewDocument ---

    public static CrewDocument toDomain(CrewDocumentEntity entity) {
        if (entity == null)
            return null;

        CrewDocument doc = new CrewDocument();
        doc.setId(entity.getId());
        doc.setCrewMemberId(entity.getCrewMember().getId());
        doc.setDocumentType(DocumentType.valueOf(entity.getDocumentType()));
        doc.setDocumentNumber(entity.getDocumentNumber());
        doc.setIssueDate(entity.getIssueDate());
        doc.setExpiryDate(entity.getExpiryDate());
        doc.setStatus(DocumentStatus.valueOf(entity.getStatus()));
        doc.setFileUrl(entity.getFileUrl());
        doc.setCreatedAt(entity.getCreatedAt());
        doc.setUpdatedAt(entity.getUpdatedAt());
        return doc;
    }

    public static CrewDocumentEntity toEntity(CrewDocument domain, CrewMemberEntity crewEntity) {
        if (domain == null)
            return null;

        return CrewDocumentEntity.builder()
                .id(domain.getId())
                .crewMember(crewEntity)
                .documentType(domain.getDocumentType().name())
                .documentNumber(domain.getDocumentNumber())
                .issueDate(domain.getIssueDate())
                .expiryDate(domain.getExpiryDate())
                .status(domain.getStatus() != null ? domain.getStatus().name() : "VALID")
                .fileUrl(domain.getFileUrl())
                .build();
    }

    // --- Bulk ---

    public static List<CrewMember> toDomainList(List<CrewMemberEntity> entities) {
        return entities.stream().map(CrewPersistenceMapper::toDomain).toList();
    }
}

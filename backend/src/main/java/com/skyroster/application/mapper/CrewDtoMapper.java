package com.skyroster.application.mapper;

import com.skyroster.application.dto.request.CrewDocumentRequest;
import com.skyroster.application.dto.request.CrewMemberRequest;
import com.skyroster.application.dto.response.CrewDocumentResponse;
import com.skyroster.application.dto.response.CrewMemberResponse;
import com.skyroster.domain.model.common.DocumentType;
import com.skyroster.domain.model.common.CrewRole;
import com.skyroster.domain.model.crew.CrewDocument;
import com.skyroster.domain.model.crew.CrewMember;

import java.time.LocalDate;
import java.util.List;

/**
 * Maps between DTOs and domain models.
 * Lives in the application layer (between controller and domain).
 */
public final class CrewDtoMapper {

    private CrewDtoMapper() {
        // Utility class
    }

    // --- Request → Domain ---

    public static CrewMember toDomain(CrewMemberRequest request) {
        CrewMember crew = new CrewMember();
        crew.setStaffId(request.getStaffId());
        crew.setFirstName(request.getFirstName());
        crew.setLastName(request.getLastName());
        crew.setCrewRole(CrewRole.valueOf(request.getCrewRole()));
        crew.setBaseStation(request.getBaseStation());
        return crew;
    }

    public static CrewDocument toDomain(CrewDocumentRequest request) {
        CrewDocument doc = new CrewDocument();
        doc.setDocumentType(DocumentType.valueOf(request.getDocumentType()));
        doc.setDocumentNumber(request.getDocumentNumber());
        doc.setIssueDate(request.getIssueDate());
        doc.setExpiryDate(request.getExpiryDate());
        doc.setFileUrl(request.getFileUrl());
        return doc;
    }

    // --- Domain → Response ---

    public static CrewMemberResponse toResponse(CrewMember crew) {
        return CrewMemberResponse.builder()
                .id(crew.getId())
                .staffId(crew.getStaffId())
                .firstName(crew.getFirstName())
                .lastName(crew.getLastName())
                .fullName(crew.getFullName())
                .crewRole(crew.getCrewRole().name())
                .crewCategory(crew.getCrewCategory().name())
                .baseStation(crew.getBaseStation())
                .status(crew.getStatus().name())
                .groundingReason(crew.getGroundingReason())
                .groundedAt(crew.getGroundedAt())
                .documents(crew.getDocuments() != null
                        ? crew.getDocuments().stream().map(CrewDtoMapper::toResponse).toList()
                        : List.of())
                .createdAt(crew.getCreatedAt())
                .updatedAt(crew.getUpdatedAt())
                .build();
    }

    public static CrewDocumentResponse toResponse(CrewDocument doc) {
        return CrewDocumentResponse.builder()
                .id(doc.getId())
                .crewMemberId(doc.getCrewMemberId())
                .documentType(doc.getDocumentType().name())
                .documentNumber(doc.getDocumentNumber())
                .issueDate(doc.getIssueDate())
                .expiryDate(doc.getExpiryDate())
                .status(doc.getStatus().name())
                .fileUrl(doc.getFileUrl())
                .daysUntilExpiry(doc.daysUntilExpiry(LocalDate.now()))
                .build();
    }

    public static List<CrewMemberResponse> toResponseList(List<CrewMember> crewList) {
        return crewList.stream().map(CrewDtoMapper::toResponse).toList();
    }

    public static List<CrewDocumentResponse> toDocResponseList(List<CrewDocument> docs) {
        return docs.stream().map(CrewDtoMapper::toResponse).toList();
    }
}

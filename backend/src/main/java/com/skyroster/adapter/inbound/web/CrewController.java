package com.skyroster.adapter.inbound.web;

import com.skyroster.application.dto.request.CrewDocumentRequest;
import com.skyroster.application.dto.request.CrewMemberRequest;
import com.skyroster.application.dto.response.CrewDocumentResponse;
import com.skyroster.application.dto.response.CrewMemberResponse;
import com.skyroster.application.mapper.CrewDtoMapper;
import com.skyroster.domain.model.crew.CrewDocument;
import com.skyroster.domain.model.crew.CrewMember;
import com.skyroster.domain.port.inbound.CrewManagementUseCase;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST controller for crew management operations.
 * Thin adapter layer — delegates all business logic to the domain service.
 * Mapped under /api/v1/crew (context-path already set in application.yml).
 */
@RestController
@RequestMapping("/crew")
public class CrewController {

    private final CrewManagementUseCase crewManagementUseCase;

    public CrewController(CrewManagementUseCase crewManagementUseCase) {
        this.crewManagementUseCase = crewManagementUseCase;
    }

    @PostMapping
    public ResponseEntity<CrewMemberResponse> registerCrew(
            @Valid @RequestBody CrewMemberRequest request) {
        CrewMember domain = CrewDtoMapper.toDomain(request);
        CrewMember created = crewManagementUseCase.registerCrew(domain);
        return ResponseEntity.status(HttpStatus.CREATED).body(CrewDtoMapper.toResponse(created));
    }

    @GetMapping
    public ResponseEntity<List<CrewMemberResponse>> listCrew(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<CrewMember> crewList = crewManagementUseCase.listCrew(role, status, search, page, size);
        return ResponseEntity.ok(CrewDtoMapper.toResponseList(crewList));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CrewMemberResponse> getCrewById(@PathVariable UUID id) {
        CrewMember crew = crewManagementUseCase.getCrewById(id);
        return ResponseEntity.ok(CrewDtoMapper.toResponse(crew));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CrewMemberResponse> updateCrew(
            @PathVariable UUID id,
            @Valid @RequestBody CrewMemberRequest request) {
        CrewMember domain = CrewDtoMapper.toDomain(request);
        CrewMember updated = crewManagementUseCase.updateCrew(id, domain);
        return ResponseEntity.ok(CrewDtoMapper.toResponse(updated));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> changeStatus(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body) {
        String action = body.getOrDefault("action", "");
        String reason = body.getOrDefault("reason", "");

        if ("GROUND".equalsIgnoreCase(action)) {
            crewManagementUseCase.groundCrew(id, reason);
        } else if ("ACTIVATE".equalsIgnoreCase(action)) {
            crewManagementUseCase.activateCrew(id);
        } else {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.noContent().build();
    }

    // --- Document Endpoints ---

    @PostMapping("/{crewId}/documents")
    public ResponseEntity<CrewDocumentResponse> addDocument(
            @PathVariable UUID crewId,
            @Valid @RequestBody CrewDocumentRequest request) {
        CrewDocument domain = CrewDtoMapper.toDomain(request);
        CrewDocument created = crewManagementUseCase.addDocument(crewId, domain);
        return ResponseEntity.status(HttpStatus.CREATED).body(CrewDtoMapper.toResponse(created));
    }

    @GetMapping("/{crewId}/documents")
    public ResponseEntity<List<CrewDocumentResponse>> getDocuments(@PathVariable UUID crewId) {
        List<CrewDocument> docs = crewManagementUseCase.getDocuments(crewId);
        return ResponseEntity.ok(CrewDtoMapper.toDocResponseList(docs));
    }

    @GetMapping("/documents/expiring")
    public ResponseEntity<List<CrewDocumentResponse>> getExpiringDocuments(
            @RequestParam(defaultValue = "30") int days) {
        List<CrewDocument> docs = crewManagementUseCase.getExpiringDocuments(days);
        return ResponseEntity.ok(CrewDtoMapper.toDocResponseList(docs));
    }
}

package com.skyroster.adapter.outbound.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * JPA entity for the crew_members table.
 * Maps to the domain CrewMember model via persistence mapper.
 */
@Entity
@Table(name = "crew_members", indexes = {
        @Index(name = "idx_crew_staff_id", columnList = "staff_id", unique = true),
        @Index(name = "idx_crew_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrewMemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "staff_id", nullable = false, unique = true, length = 20)
    private String staffId;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "crew_role", nullable = false, length = 30)
    private String crewRole;

    @Column(name = "crew_category", nullable = false, length = 20)
    private String crewCategory;

    @Column(name = "base_station", nullable = false, length = 5)
    private String baseStation;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "ACTIVE";

    @Column(name = "grounding_reason")
    private String groundingReason;

    @Column(name = "grounded_at")
    private Instant groundedAt;

    @OneToMany(mappedBy = "crewMember", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CrewDocumentEntity> documents = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    private Long version;
}

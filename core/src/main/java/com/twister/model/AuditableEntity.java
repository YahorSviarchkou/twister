package com.twister.model;

import com.twister.service.AuditorProvider;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode
@MappedSuperclass
public abstract class AuditableEntity {

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false, updatable = false)
    private String createdBy;

    private Instant updatedAt;

    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        createdAt = getCurrentInstant();
        createdBy = AuditorProvider.getAuditor();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = getCurrentInstant();
        updatedBy = AuditorProvider.getAuditor();
    }

    protected Instant getCurrentInstant() {
        return Instant.now();
    }
}

package com.twister.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Сущность, объединяющая сервис с запчастями
 */
@Data
@Entity
@Table(name = "operation_spares")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OperationSpares {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Long operationId;

    String spareIds;

    public Set<Long> getSpareIds() {
        if (spareIds == null || spareIds.isBlank()) return Set.of();
        return Arrays.stream(spareIds.split(","))
                .map(String::trim)
                .map(Long::valueOf)
                .collect(Collectors.toSet());
    }

    public void setSpareIds(List<Long> ids) {
        this.spareIds = ids.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }
}

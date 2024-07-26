package com.twister.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Сущность, описывающая запчасть
 */
@Data
@Entity
@Table(name = "spares")
@ToString(exclude = "operations")
@EqualsAndHashCode(exclude = "operations")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Spare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String title;
    BigDecimal cost;

    @ManyToMany
    @JoinTable(
            name = "spare_operations",
            joinColumns = @JoinColumn(name = "spare_id"),
            inverseJoinColumns = @JoinColumn(name = "operation_id")
    )
    Set<Operation> operations = new HashSet<>();
}

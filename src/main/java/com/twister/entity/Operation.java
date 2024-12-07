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
 * Сущность, описывающая возможные работы(Операции) над транспортом
 */
@Data
@Entity
@Table(name = "operations")
@ToString(exclude = "works")
@EqualsAndHashCode(exclude = "works")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Operation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String title;
    BigDecimal cost;

    @ManyToMany(mappedBy = "operations")
    Set<Spare> spares = new HashSet<>();

    @ManyToMany(mappedBy = "operations")
    Set<Work> works = new HashSet<>();
}

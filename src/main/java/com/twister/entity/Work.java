package com.twister.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "works")
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Work {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String title;

    @ManyToOne
    @JoinColumn(name = "client_id")
    Client client;

    @ManyToOne
    @JoinColumn(name = "transport_id")
    Transport transport;

    @CreationTimestamp
    @Column(updatable = false)
    Date created;

    @UpdateTimestamp
    Date updated;

    Date completed;

    @Transient
    BigDecimal totalCost;

    @OneToMany(mappedBy = "work")
    Set<Task> tasks = new HashSet<>();

//    public BigDecimal getTotalCost() {
//        return tasks.stream()
//                .reduce(BigDecimal::add)
//                .orElse(null);
//    }
}

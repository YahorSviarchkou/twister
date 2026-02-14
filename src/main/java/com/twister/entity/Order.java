package com.twister.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Сущность, описывающая выполненные работы(заказы) клиента
 */
@Data
@Entity
@Table(name = "orders")
@ToString(exclude = "opSps")
@EqualsAndHashCode(exclude = "opSps")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String notes;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    Customer customer;

    @CreationTimestamp
    @Column(updatable = false)
    Date created;

    //todo exclude eager fetch type
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "order_op_sps",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "operation_spares_id")
    )
    Set<OperationSpares> opSps = new HashSet<>();
}

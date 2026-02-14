package com.twister.entity;

import com.twister.configuration.annotation.UIColumn;
import com.twister.ui.TableModel;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

/**
 * Сущность, описывающая клиента
 */
@Data
@Entity
@Table(name = "customers")
@ToString(exclude = "orders")
@EqualsAndHashCode(exclude = "orders")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Customer implements TableModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @UIColumn("table.id.column")
    Long id;

    @UIColumn("table.name.column")
    String name;

    @UIColumn("table.surname.column")
    String surname;

    @UIColumn("table.patronymic.column")
    String patronymic;

    @UIColumn("table.phone.column")
    String phone;

    @OneToMany(mappedBy = "customer")
    Set<Order> orders = new HashSet<>();

    public String getFullName() {
        return String.join(" ", surname, name, patronymic);
    }
}

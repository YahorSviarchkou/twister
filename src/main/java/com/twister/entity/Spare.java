package com.twister.entity;

import com.twister.configuration.annotation.UIColumn;
import com.twister.ui.TableModel;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

/**
 * Сущность, описывающая запчасть
 */
@Data
@Entity
@Table(name = "spares")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Spare implements TableModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @UIColumn("table.id.column")
    Long id;

    @UIColumn("table.title.column")
    String title;

    @UIColumn("table.cost.column")
    BigDecimal cost;
}

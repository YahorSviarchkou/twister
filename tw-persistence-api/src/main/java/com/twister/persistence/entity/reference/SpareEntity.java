package com.twister.persistence.entity.reference;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Table(name = "spares")
@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SpareEntity extends CompositeReferenceEntity<SpareTypeEntity, SpareBrandEntity> {

    @ManyToOne
    @JoinColumn(name = "transport_type_id")
    private TransportTypeEntity transportType;

    private String material;

    private Integer warehouseQuantity;

    private BigDecimal price;
}

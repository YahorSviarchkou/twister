package com.twister.domain.reference;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;


@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Spare extends CompositeReference<SpareType, SpareBrand> {

    private TransportType transportType;
    private String material;
    private Integer warehouseQuantity;
    private BigDecimal price;
}

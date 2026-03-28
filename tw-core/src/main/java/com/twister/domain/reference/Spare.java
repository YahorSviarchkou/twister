package com.twister.domain.reference;

import java.math.BigDecimal;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Spare extends CompositeReference<SpareType, SpareBrand> {

    private TransportType transportType;
    private String material;
    private Integer warehouseQuantity;
    private BigDecimal price;
}

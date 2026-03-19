package com.twister.domain.repair;

import com.twister.domain.AuditableModel;
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
public class RepairInvoice extends AuditableModel {

    private Long id;
    private String description;
    private RepairOrder order;
    private BigDecimal diagnosticPrice;
    private BigDecimal sparePrice;
    private BigDecimal laborPrice;
}

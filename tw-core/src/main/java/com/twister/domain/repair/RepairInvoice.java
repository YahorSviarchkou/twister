package com.twister.domain.repair;

import com.twister.domain.AuditableModel;
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
public class RepairInvoice extends AuditableModel {

    private Long id;
    private String description;
    private RepairOrder order;
    private BigDecimal diagnosticPrice;
    private BigDecimal sparePrice;
    private BigDecimal laborPrice;
}

package com.twister.domain.repair;

import com.twister.domain.AuditableModel;
import com.twister.domain.Customer;
import com.twister.domain.reference.Transport;
import java.util.List;
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
public class RepairOrder extends AuditableModel {

    private Long id;
    private String description;
    private Customer customer;
    private Transport transport;
    private List<RepairOrderStatusHistory> statusHistory;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private RepairTask task;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private RepairInvoice invoice;
}

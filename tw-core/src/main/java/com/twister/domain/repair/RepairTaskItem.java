package com.twister.domain.repair;

import com.twister.domain.AuditableModel;
import com.twister.domain.reference.ServiceType;
import com.twister.domain.reference.Spare;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class RepairTaskItem extends AuditableModel {

    private Long id;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private RepairTask task;

    private ServiceType serviceType;
    private List<Spare> spares;
    private List<RepairTaskItemStatusHistory> statusHistory;
}

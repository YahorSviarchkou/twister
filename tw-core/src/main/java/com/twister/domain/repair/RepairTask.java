package com.twister.domain.repair;

import com.twister.domain.AuditableModel;
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
public class RepairTask extends AuditableModel {

    private Long id;
    private RepairOrder order;
    private List<RepairTaskItem> items;
    private List<RepairTaskStatusHistory> statusHistory;
}

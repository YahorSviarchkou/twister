package com.twister.persistence.entity.repair;

import com.twister.domain.repair.RepairTaskStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Table(name = "repair_task_status_history")
@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class RepairTaskStatusHistoryEntity
    extends StatusHistoryEntity<RepairTaskEntity, RepairTaskStatus> {
}

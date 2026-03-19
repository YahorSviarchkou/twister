package com.twister.workflow.dao.repair;

import com.twister.domain.repair.RepairTaskItem;

import java.util.List;
import java.util.Optional;

public interface RepairTaskItemDao {

    Optional<RepairTaskItem> findById(Long id);

    List<RepairTaskItem> findRepairTaskItemByTaskId(Long taskId);

    RepairTaskItem save(RepairTaskItem repairTaskItem);
}

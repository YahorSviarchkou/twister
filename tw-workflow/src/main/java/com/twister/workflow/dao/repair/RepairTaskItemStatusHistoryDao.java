package com.twister.workflow.dao.repair;

import com.twister.domain.repair.RepairTaskItemStatus;
import com.twister.domain.repair.RepairTaskItemStatusHistory;
import java.util.List;

public interface RepairTaskItemStatusHistoryDao extends StatusHistoryDao<RepairTaskItemStatusHistory> {

    List<RepairTaskItemStatus> findAllTaskItemsStatusesByTaskId(Long taskId);
}

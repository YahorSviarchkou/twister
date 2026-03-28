package com.twister.workflow.service.repair;

import com.twister.domain.repair.RepairTaskItem;
import com.twister.domain.repair.RepairTaskItemStatus;
import com.twister.domain.repair.RepairTaskItemStatusHistory;
import com.twister.workflow.dao.repair.RepairTaskItemStatusHistoryDao;
import com.twister.workflow.dao.repair.StatusHistoryDao;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RepairTaskItemStatusHistoryService
        extends StatusHistoryService<RepairTaskItem, RepairTaskItemStatusHistory, RepairTaskItemStatus> {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private final RepairTaskItemStatusHistoryDao repairTaskItemStatusHistoryDao;

    @Override
    public Class<RepairTaskItem> getWorkflowClass() {
        return RepairTaskItem.class;
    }

    public List<RepairTaskItemStatus> getAllTaskItemsStatusesByTaskId(Long taskId) {
        return repairTaskItemStatusHistoryDao.findAllTaskItemsStatusesByTaskId(taskId);
    }

    @Override
    protected StatusHistoryDao<RepairTaskItemStatusHistory> getStatusHistoryDao() {
        return repairTaskItemStatusHistoryDao;
    }

    @Override
    protected RepairTaskItemStatusHistory buildHistory(Long workflowItemId, RepairTaskItemStatus status) {
        return RepairTaskItemStatusHistory.builder()
                .workflowItemId(workflowItemId)
                .status(status)
                .build();
    }
}

package com.twister.workflow.service.repair;

import com.twister.domain.repair.RepairTaskItem;
import com.twister.domain.repair.RepairTaskItemStatus;
import com.twister.domain.repair.RepairTaskItemStatusHistory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RepairTaskItemStatusHistoryService
        extends StatusHistoryService<RepairTaskItem, RepairTaskItemStatusHistory, RepairTaskItemStatus> {

    private final RepairTaskItemService repairTaskItemService;

    @Override
    public Class<RepairTaskItem> getWorkflowClass() {
        return RepairTaskItem.class;
    }

    @Override
    protected Class<RepairTaskItemStatusHistory> getStatusHistoryClass() {
        return RepairTaskItemStatusHistory.class;
    }

    @Override
    protected RepairTaskItemStatusHistory buildHistory(Long workflowItem, RepairTaskItemStatus status) {
        RepairTaskItem repairTaskItem = repairTaskItemService.getRepairTaskItemById(workflowItem);
        return RepairTaskItemStatusHistory.builder()
                .workflowItem(repairTaskItem)
                .status(status)
                .build();
    }

    public List<RepairTaskItemStatusHistory> getLastWorkflowStatusesByTaskId(Long taskId) {
        throw new UnsupportedOperationException();
    }
}

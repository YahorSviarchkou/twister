package com.twister.workflow.service.repair;

import com.twister.domain.repair.RepairTask;
import com.twister.domain.repair.RepairTaskStatus;
import com.twister.domain.repair.RepairTaskStatusHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RepairTaskStatusHistoryService
    extends StatusHistoryService<RepairTask, RepairTaskStatusHistory, RepairTaskStatus> {

    private final RepairTaskService repairTaskService;

    @Override
    public Class<RepairTask> getWorkflowClass() {
        return RepairTask.class;
    }

    @Override
    protected Class<RepairTaskStatusHistory> getStatusHistoryClass() {
        return RepairTaskStatusHistory.class;
    }

    @Override
    protected RepairTaskStatusHistory buildHistory(Long workflowItemId, RepairTaskStatus status) {
        RepairTask repairTask = repairTaskService.getRepairTaskById(workflowItemId);
        return RepairTaskStatusHistory.builder()
            .workflowItem(repairTask)
            .status(status)
            .build();
    }
}

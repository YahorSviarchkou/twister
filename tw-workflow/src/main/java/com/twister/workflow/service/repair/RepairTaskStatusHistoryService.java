package com.twister.workflow.service.repair;

import com.twister.domain.repair.RepairTask;
import com.twister.domain.repair.RepairTaskStatus;
import com.twister.domain.repair.RepairTaskStatusHistory;
import com.twister.workflow.dao.repair.StatusHistoryDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RepairTaskStatusHistoryService
        extends StatusHistoryService<RepairTask, RepairTaskStatusHistory, RepairTaskStatus> {

    private final StatusHistoryDao<RepairTaskStatusHistory> repairTaskStatusHistoryDao;

    @Override
    public Class<RepairTask> getWorkflowClass() {
        return RepairTask.class;
    }

    @Override
    protected StatusHistoryDao<RepairTaskStatusHistory> getStatusHistoryDao() {
        return repairTaskStatusHistoryDao;
    }

    @Override
    protected RepairTaskStatusHistory buildHistory(Long workflowItemId, RepairTaskStatus status) {
        return RepairTaskStatusHistory.builder()
                .workflowItemId(workflowItemId)
                .status(status)
                .build();
    }
}

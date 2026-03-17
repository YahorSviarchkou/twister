package com.twister.service.workflow.event;

import com.twister.domain.repair.RepairTask;

public enum RepairTaskEvent implements WorkflowEvent<RepairTask> {
    START_DIAGNOSIS,
    START_REPAIR,
    FINISH_TASK,
    CANCEL;

    @Override
    public Class<RepairTask> getWorkflowType() {
        return RepairTask.class;
    }
}

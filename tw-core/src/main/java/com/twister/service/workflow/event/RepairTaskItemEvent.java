package com.twister.service.workflow.event;

import com.twister.domain.repair.RepairTaskItem;

public enum RepairTaskItemEvent implements WorkflowEvent<RepairTaskItem> {
    START_WORK,
    FINISH_WORK,
    CANCEL,
    WAIT_PARTS,
    PARTS_AVAILABLE;

    @Override
    public Class<RepairTaskItem> getWorkflowType() {
        return RepairTaskItem.class;
    }
}

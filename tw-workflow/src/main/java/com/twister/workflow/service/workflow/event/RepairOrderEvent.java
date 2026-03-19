package com.twister.workflow.service.workflow.event;

import com.twister.domain.repair.RepairOrder;

public enum RepairOrderEvent implements WorkflowEvent<RepairOrder> {
    ACCEPT_ORDER,
    REJECT_ORDER,
    GENERATE_INVOICE,
    PAY,
    CLOSE;

    @Override
    public Class<RepairOrder> getWorkflowType() {
        return RepairOrder.class;
    }
}

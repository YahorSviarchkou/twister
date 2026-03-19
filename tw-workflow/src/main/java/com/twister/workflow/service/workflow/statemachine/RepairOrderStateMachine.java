package com.twister.workflow.service.workflow.statemachine;

import com.twister.domain.repair.RepairOrder;
import com.twister.domain.repair.RepairOrderStatus;
import com.twister.workflow.service.workflow.TransitionTable;
import com.twister.workflow.service.workflow.event.RepairOrderEvent;
import org.springframework.stereotype.Service;

@Service
public class RepairOrderStateMachine extends StateMachine<RepairOrderStatus, RepairOrderEvent> {

    public RepairOrderStateMachine() {
        super(TransitionTable.REPAIR_ORDER_TRANSITIONS);
    }

    @Override
    public Class<?> getWorkflowClass() {
        return RepairOrder.class;
    }
}

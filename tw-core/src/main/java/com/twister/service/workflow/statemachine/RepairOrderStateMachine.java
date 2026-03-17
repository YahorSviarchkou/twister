package com.twister.service.workflow.statemachine;

import com.twister.domain.repair.RepairOrder;
import com.twister.domain.repair.RepairOrderStatus;
import com.twister.service.workflow.TransitionTable;
import com.twister.service.workflow.event.RepairOrderEvent;
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

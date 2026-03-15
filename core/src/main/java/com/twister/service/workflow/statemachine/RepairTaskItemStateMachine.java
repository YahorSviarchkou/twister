package com.twister.service.workflow.statemachine;

import com.twister.model.repair.RepairTaskItem;
import com.twister.model.repair.RepairTaskItemStatus;
import com.twister.service.workflow.TransitionTable;
import com.twister.service.workflow.event.RepairTaskItemEvent;
import org.springframework.stereotype.Service;

@Service
public class RepairTaskItemStateMachine extends StateMachine<RepairTaskItemStatus, RepairTaskItemEvent> {

    public RepairTaskItemStateMachine() {
        super(TransitionTable.REPAIR_TASK_ITEM_TRANSITIONS);
    }

    @Override
    public Class<?> getWorkflowClass() {
        return RepairTaskItem.class;
    }
}

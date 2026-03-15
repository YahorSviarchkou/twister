package com.twister.service.workflow.statemachine;

import com.twister.model.repair.RepairTask;
import com.twister.model.repair.RepairTaskStatus;
import com.twister.service.workflow.TransitionTable;
import com.twister.service.workflow.event.RepairTaskEvent;
import org.springframework.stereotype.Service;

@Service
public class RepairTaskStateMachine extends StateMachine<RepairTaskStatus, RepairTaskEvent> {

    public RepairTaskStateMachine() {
        super(TransitionTable.REPAIR_TASK_TRANSITIONS);
    }

    @Override
    public Class<?> getWorkflowClass() {
        return RepairTask.class;
    }
}

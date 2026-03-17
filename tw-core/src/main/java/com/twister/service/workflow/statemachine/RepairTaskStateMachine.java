package com.twister.service.workflow.statemachine;

import com.twister.domain.repair.RepairTask;
import com.twister.domain.repair.RepairTaskStatus;
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

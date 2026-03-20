package com.twister.workflow.service.workflow;

import com.twister.workflow.service.repair.StatusHistoryService;
import com.twister.workflow.service.workflow.event.WorkflowEvent;
import com.twister.workflow.service.workflow.statemachine.StateMachine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowEventHandler {

    private final WorkflowRegistry workflowRegistry;

    public <S, E extends WorkflowEvent<?>> S applyEvent(E event, Long workflowItemId) {
        log.info("Handling event: {}, for workflow item id: {}", event, workflowItemId);
        StatusHistoryService<?, ?, S> statusHistoryService =
                workflowRegistry.getStatusHistoryService(event.getWorkflowType());

        S current = statusHistoryService.getLastWorkflowStatus(workflowItemId);

        StateMachine<S, E> stateMachine = workflowRegistry.getStateMachine(event.getWorkflowType());
        S next = stateMachine.next(current, event);

        return statusHistoryService.updateWorkflowStatus(workflowItemId, next);
    }
}

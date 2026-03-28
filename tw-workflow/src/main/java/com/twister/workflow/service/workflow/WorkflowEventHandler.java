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
        if (event == null) {
            throw new IllegalArgumentException("Event must be exist");
        }
        log.info(
                "Handling event: {}, for {} workflow item with id: {}",
                event,
                event.getWorkflowType().getSimpleName(),
                workflowItemId);

        WorkflowService<?> workflowService = workflowRegistry.getWorkflowService(event.getClass());
        if (!workflowService.existsById(workflowItemId)) {
            throw new IllegalArgumentException(
                    event.getWorkflowType().getSimpleName() + " workflow item not found by id: " + workflowItemId);
        }

        StatusHistoryService<?, ?, S> statusHistoryService =
                workflowRegistry.getStatusHistoryService(event.getWorkflowType());

        S current = statusHistoryService.getLastWorkflowStatus(workflowItemId);

        StateMachine<S, E> stateMachine = workflowRegistry.getStateMachine(event.getWorkflowType());
        S next = stateMachine.next(current, event);

        return statusHistoryService.updateWorkflowStatus(workflowItemId, next);
    }
}

package com.twister.workflow.service.workflow;

import com.twister.workflow.service.repair.StatusHistoryService;
import com.twister.workflow.service.workflow.statemachine.StateMachine;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkflowRegistry {

    private final List<StatusHistoryService<?, ?, ?>> statusHistoryServices;
    private final List<StateMachine<?, ?>> stateMachines;

    private Map<Class<?>, StatusHistoryService<?, ?, ?>> workflowItemStatusHistoryService;
    private Map<Class<?>, StateMachine<?, ?>> workflowItemStateMachine;

    @PostConstruct
    public void init() {
        workflowItemStatusHistoryService = statusHistoryServices.stream()
            .collect(Collectors.toMap(
                StatusHistoryService::getWorkflowClass,
                Function.identity()
            ));
        workflowItemStateMachine = stateMachines.stream()
            .collect(Collectors.toMap(
                StateMachine::getWorkflowClass,
                Function.identity()
            ));
    }

    public <S> StatusHistoryService<?, ?, S> getStatusHistoryService(
        Class<?> workflowClass
    ) {
        StatusHistoryService<?, ?, ?> statusHistoryService = workflowItemStatusHistoryService.get(workflowClass);
        if (statusHistoryService == null) {
            throw new NoSuchElementException(
                "StatusHistoryService not found for workflow: %s".formatted(workflowClass.getSimpleName())
            );
        }
        // noinspection unchecked
        return (StatusHistoryService<?, ?, S>) statusHistoryService;
    }

    public <S, E> StateMachine<S, E> getStateMachine(
        Class<?> workflowClass
    ) {
        StateMachine<?, ?> stateMachine = workflowItemStateMachine.get(workflowClass);
        if (stateMachine == null) {
            throw new NoSuchElementException(
                "StateMachine not found for workflow: %s".formatted(workflowClass.getSimpleName())
            );
        }
        // noinspection unchecked
        return (StateMachine<S, E>) stateMachine;
    }
}

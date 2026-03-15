package com.twister.service.workflow.statemachine;

import com.twister.service.workflow.Transition;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class StateMachine<S, E> {

    private final Map<S, Map<E, S>> transitions;

    public abstract Class<?> getWorkflowClass();

    public StateMachine(List<Transition<S, E>> transitions) {
        this.transitions = transitions.stream()
            .collect(Collectors.groupingBy(
                Transition::fromStatus,
                Collectors.toMap(
                    Transition::event,
                    Transition::toStatus
                )
            ));
    }

    public S next(S current, E event) {
        Map<E, S> map = transitions.get(current);

        if (map == null || !map.containsKey(event)) {
            throw new IllegalStateException(
                "Invalid transition %s -> %s".formatted(current, event)
            );
        }

        return map.get(event);
    }
}

package com.twister.workflow.service.workflow;

public record Transition<S, E>(
    S fromStatus,
    E event,
    S toStatus
) {
}

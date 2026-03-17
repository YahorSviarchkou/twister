package com.twister.service.workflow;

public record Transition<S, E>(
    S fromStatus,
    E event,
    S toStatus
) {
}

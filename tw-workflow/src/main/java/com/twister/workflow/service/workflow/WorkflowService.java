package com.twister.workflow.service.workflow;

import com.twister.workflow.service.workflow.event.WorkflowEvent;

public interface WorkflowService<T> {

    Class<? extends WorkflowEvent<T>> getWorkflowEvent();

    boolean existsById(Long id);
}

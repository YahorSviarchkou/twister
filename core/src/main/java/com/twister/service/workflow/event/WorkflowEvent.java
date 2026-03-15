package com.twister.service.workflow.event;

public interface WorkflowEvent<T> {

    Class<T> getWorkflowType();
}

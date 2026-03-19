package com.twister.workflow.service.workflow.event;

public interface WorkflowEvent<T> {

    Class<T> getWorkflowType();
}

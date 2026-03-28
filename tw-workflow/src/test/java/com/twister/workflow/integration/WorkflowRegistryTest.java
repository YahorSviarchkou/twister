package com.twister.workflow.integration;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.twister.domain.repair.RepairOrder;
import com.twister.domain.repair.RepairTask;
import com.twister.domain.repair.RepairTaskItem;
import com.twister.workflow.service.repair.RepairOrderService;
import com.twister.workflow.service.repair.RepairOrderStatusHistoryService;
import com.twister.workflow.service.repair.RepairTaskItemService;
import com.twister.workflow.service.repair.RepairTaskItemStatusHistoryService;
import com.twister.workflow.service.repair.RepairTaskService;
import com.twister.workflow.service.repair.RepairTaskStatusHistoryService;
import com.twister.workflow.service.workflow.WorkflowRegistry;
import com.twister.workflow.service.workflow.event.RepairOrderEvent;
import com.twister.workflow.service.workflow.event.RepairTaskEvent;
import com.twister.workflow.service.workflow.event.RepairTaskItemEvent;
import com.twister.workflow.service.workflow.statemachine.RepairOrderStateMachine;
import com.twister.workflow.service.workflow.statemachine.RepairTaskItemStateMachine;
import com.twister.workflow.service.workflow.statemachine.RepairTaskStateMachine;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("AssertBetweenInconvertibleTypes")
public class WorkflowRegistryTest extends BaseIntegrationTest {

    @Autowired
    private WorkflowRegistry registry;

    @Autowired
    private RepairTaskStateMachine repairTaskStateMachine;

    @Autowired
    private RepairOrderStateMachine repairOrderStateMachine;

    @Autowired
    private RepairTaskItemStateMachine repairTaskItemStateMachine;

    @Autowired
    private RepairTaskService repairTaskService;

    @Autowired
    private RepairOrderService repairOrderService;

    @Autowired
    private RepairTaskItemService repairTaskItemService;

    @Autowired
    private RepairTaskStatusHistoryService repairTaskStatusHistoryService;

    @Autowired
    private RepairOrderStatusHistoryService repairOrderStatusHistoryService;

    @Autowired
    private RepairTaskItemStatusHistoryService repairTaskItemStatusHistoryService;

    @Test
    void testGetStateMachinesWhenWorkflowClassExist() {
        assertSame(repairTaskStateMachine, registry.getStateMachine(RepairTask.class));
        assertSame(repairOrderStateMachine, registry.getStateMachine(RepairOrder.class));
        assertSame(repairTaskItemStateMachine, registry.getStateMachine(RepairTaskItem.class));
    }

    @Test
    void testGetStateMachinesWhenWorkflowClassNotExist() {
        assertThrows(NoSuchElementException.class, () -> registry.getStateMachine(Object.class));
    }

    @Test
    void testGetWorkflowServiceWhenWorkflowEventExist() {
        assertSame(repairTaskService, registry.getWorkflowService(RepairTaskEvent.class));
        assertSame(repairOrderService, registry.getWorkflowService(RepairOrderEvent.class));
        assertSame(repairTaskItemService, registry.getWorkflowService(RepairTaskItemEvent.class));
    }

    @Test
    void testGetWorkflowServiceWhenWorkflowEventNotExist() {
        assertThrows(NoSuchElementException.class, () -> registry.getWorkflowService(Object.class));
    }

    @Test
    void testGetStatusHistoryServiceWhenWorkflowClassExist() {
        assertSame(repairTaskStatusHistoryService, registry.getStatusHistoryService(RepairTask.class));
        assertSame(repairOrderStatusHistoryService, registry.getStatusHistoryService(RepairOrder.class));
        assertSame(repairTaskItemStatusHistoryService, registry.getStatusHistoryService(RepairTaskItem.class));
    }

    @Test
    void testGetStatusHistoryServiceWhenWorkflowClassNotExist() {
        assertThrows(NoSuchElementException.class, () -> registry.getStatusHistoryService(Object.class));
    }
}

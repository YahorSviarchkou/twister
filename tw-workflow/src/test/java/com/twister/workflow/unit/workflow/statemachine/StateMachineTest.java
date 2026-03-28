package com.twister.workflow.unit.workflow.statemachine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.twister.domain.repair.RepairOrderStatus;
import com.twister.domain.repair.RepairTaskItemStatus;
import com.twister.domain.repair.RepairTaskStatus;
import com.twister.workflow.service.workflow.event.RepairOrderEvent;
import com.twister.workflow.service.workflow.event.RepairTaskEvent;
import com.twister.workflow.service.workflow.event.RepairTaskItemEvent;
import com.twister.workflow.service.workflow.statemachine.RepairOrderStateMachine;
import com.twister.workflow.service.workflow.statemachine.RepairTaskItemStateMachine;
import com.twister.workflow.service.workflow.statemachine.RepairTaskStateMachine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StateMachineTest {

    private RepairOrderStateMachine orderStateMachine;
    private RepairTaskStateMachine taskStateMachine;
    private RepairTaskItemStateMachine itemStateMachine;

    @BeforeEach
    void setUp() {
        orderStateMachine = new RepairOrderStateMachine();
        taskStateMachine = new RepairTaskStateMachine();
        itemStateMachine = new RepairTaskItemStateMachine();
    }

    @Test
    void testRepairOrderStateMachine_ValidTransitions() {
        assertEquals(
                RepairOrderStatus.ACCEPTED,
                orderStateMachine.next(RepairOrderStatus.OPEN, RepairOrderEvent.ACCEPT_ORDER));
        assertEquals(
                RepairOrderStatus.WAITING_PAYMENT,
                orderStateMachine.next(RepairOrderStatus.ACCEPTED, RepairOrderEvent.GENERATE_INVOICE));
        assertEquals(
                RepairOrderStatus.CLOSED,
                orderStateMachine.next(RepairOrderStatus.WAITING_PAYMENT, RepairOrderEvent.PAY));

        assertEquals(
                RepairOrderStatus.REJECTED,
                orderStateMachine.next(RepairOrderStatus.OPEN, RepairOrderEvent.REJECT_ORDER));
        assertEquals(
                RepairOrderStatus.REJECTED,
                orderStateMachine.next(RepairOrderStatus.ACCEPTED, RepairOrderEvent.REJECT_ORDER));
    }

    @Test
    void testRepairOrderStateMachine_InvalidTransition() {
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> orderStateMachine.next(RepairOrderStatus.OPEN, RepairOrderEvent.PAY));
        assertTrue(exception.getMessage().contains("Invalid transition"));
    }

    @Test
    void testRepairTaskStateMachine_ValidTransitions() {
        assertEquals(
                RepairTaskStatus.DIAGNOSIS,
                taskStateMachine.next(RepairTaskStatus.CREATED, RepairTaskEvent.START_DIAGNOSIS));
        assertEquals(
                RepairTaskStatus.IN_PROGRESS,
                taskStateMachine.next(RepairTaskStatus.DIAGNOSIS, RepairTaskEvent.START_REPAIR));
        assertEquals(
                RepairTaskStatus.READY,
                taskStateMachine.next(RepairTaskStatus.IN_PROGRESS, RepairTaskEvent.FINISH_TASK));

        assertEquals(
                RepairTaskStatus.CANCELED, taskStateMachine.next(RepairTaskStatus.CREATED, RepairTaskEvent.CANCEL));
    }

    @Test
    void testRepairTaskStateMachine_InvalidTransition() {
        assertThrows(
                IllegalStateException.class,
                () -> taskStateMachine.next(RepairTaskStatus.READY, RepairTaskEvent.CANCEL));
    }

    @Test
    void testRepairTaskItemStateMachine_ValidTransitions() {
        assertEquals(
                RepairTaskItemStatus.WAITING_PARTS,
                itemStateMachine.next(RepairTaskItemStatus.CREATED, RepairTaskItemEvent.WAIT_PARTS));
        assertEquals(
                RepairTaskItemStatus.IN_PROGRESS,
                itemStateMachine.next(RepairTaskItemStatus.WAITING_PARTS, RepairTaskItemEvent.PARTS_AVAILABLE));
        assertEquals(
                RepairTaskItemStatus.IN_PROGRESS,
                itemStateMachine.next(RepairTaskItemStatus.CREATED, RepairTaskItemEvent.START_WORK));
        assertEquals(
                RepairTaskItemStatus.READY,
                itemStateMachine.next(RepairTaskItemStatus.IN_PROGRESS, RepairTaskItemEvent.FINISH_WORK));

        assertEquals(
                RepairTaskItemStatus.CANCELED,
                itemStateMachine.next(RepairTaskItemStatus.CREATED, RepairTaskItemEvent.CANCEL));
    }

    @Test
    void testRepairTaskItemStateMachine_InvalidTransition() {
        assertThrows(
                IllegalStateException.class,
                () -> itemStateMachine.next(RepairTaskItemStatus.READY, RepairTaskItemEvent.CANCEL));
    }
}

package com.twister.workflow.unit.workflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.twister.domain.repair.RepairOrder;
import com.twister.domain.repair.RepairOrderStatus;
import com.twister.domain.repair.RepairTask;
import com.twister.domain.repair.RepairTaskItem;
import com.twister.domain.repair.RepairTaskItemStatus;
import com.twister.domain.repair.RepairTaskStatus;
import com.twister.workflow.service.repair.RepairOrderService;
import com.twister.workflow.service.repair.RepairTaskItemService;
import com.twister.workflow.service.repair.RepairTaskItemStatusHistoryService;
import com.twister.workflow.service.repair.RepairTaskService;
import com.twister.workflow.service.workflow.WorkflowEventHandler;
import com.twister.workflow.service.workflow.WorkflowOrchestrator;
import com.twister.workflow.service.workflow.event.RepairOrderEvent;
import com.twister.workflow.service.workflow.event.RepairTaskEvent;
import com.twister.workflow.service.workflow.event.RepairTaskItemEvent;
import com.twister.workflow.service.workflow.event.WorkflowEvent;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class WorkflowOrchestratorTest {

    private static final Long ID_1 = 1L;
    private static final Long ORDER_ID = 10L;
    private static final Long TASK_ID = 100L;
    private static final Long TASK_ITEM_ID = 1000L;

    @Mock
    private WorkflowEventHandler eventHandler;

    @Mock
    private RepairOrderService repairOrderService;

    @Mock
    private RepairTaskService repairTaskService;

    @Mock
    private RepairTaskItemService repairTaskItemService;

    @Mock
    private RepairTaskItemStatusHistoryService repairTaskItemStatusHistoryService;

    @Mock
    private WorkflowOrchestrator self;

    @InjectMocks
    private WorkflowOrchestrator orchestrator;

    @Captor
    private ArgumentCaptor<RepairTask> taskCaptor;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(orchestrator, "self", self);
    }

    @Test
    void testExecuteWhenEventIsNull() {
        // Then
        assertThrows(IllegalArgumentException.class, () -> orchestrator.execute(null, ID_1));
    }

    @Test
    void testExecuteWhenWorkflowItemIdIsNull() {
        // Then
        assertThrows(IllegalArgumentException.class, () -> orchestrator.execute(RepairOrderEvent.ACCEPT_ORDER, null));
    }

    @Test
    void testCascadeFlowWhenOrderAccepted() {
        // Given
        RepairOrder order = RepairOrder.builder().id(ORDER_ID).build();
        // When
        when(eventHandler.applyEvent(RepairOrderEvent.ACCEPT_ORDER, ORDER_ID)).thenReturn(RepairOrderStatus.ACCEPTED);
        when(repairOrderService.getRepairOrderById(ORDER_ID)).thenReturn(order);
        // Then
        orchestrator.execute(RepairOrderEvent.ACCEPT_ORDER, ORDER_ID);

        verify(repairTaskService, times(1)).create(taskCaptor.capture());
        RepairTask result = taskCaptor.getValue();

        assertEquals(order, result.getOrder());
    }

    @Test
    void testCascadeFlowWhenOrderRejected() {
        // Given
        RepairTask task = RepairTask.builder().id(TASK_ID).build();
        RepairTaskItem item = RepairTaskItem.builder().id(TASK_ITEM_ID).build();
        // When
        when(eventHandler.applyEvent(RepairOrderEvent.REJECT_ORDER, ORDER_ID)).thenReturn(RepairOrderStatus.REJECTED);
        when(eventHandler.applyEvent(RepairTaskEvent.CANCEL, TASK_ID)).thenReturn(RepairTaskStatus.CANCELED);
        when(eventHandler.applyEvent(RepairTaskItemEvent.CANCEL, TASK_ITEM_ID))
                .thenReturn(RepairTaskItemStatus.CANCELED);
        when(repairTaskService.getRepairTaskByOrderId(ORDER_ID)).thenReturn(task);
        when(repairTaskItemService.getAllByTaskId(TASK_ID)).thenReturn(List.of(item));
        doAnswer(this::orchestratorExecute).when(self).execute(any(), anyLong());
        // Then
        orchestrator.execute(RepairOrderEvent.REJECT_ORDER, ORDER_ID);

        verify(self, times(1)).execute(RepairTaskEvent.CANCEL, TASK_ID);
        verify(self, times(1)).execute(RepairTaskItemEvent.CANCEL, TASK_ITEM_ID);
    }

    @Test
    void testCascadeFlowWhenAllTaskItemsReady() {
        // Given
        RepairTask task = RepairTask.builder().id(TASK_ID).build();
        RepairTaskItem item =
                RepairTaskItem.builder().id(TASK_ITEM_ID).task(task).build();
        RepairOrder order = RepairOrder.builder().id(ORDER_ID).build();
        // When
        when(eventHandler.applyEvent(RepairTaskItemEvent.FINISH_WORK, TASK_ITEM_ID))
                .thenReturn(RepairTaskItemStatus.READY);
        when(eventHandler.applyEvent(RepairTaskEvent.FINISH_TASK, TASK_ID)).thenReturn(RepairTaskStatus.READY);
        when(eventHandler.applyEvent(RepairOrderEvent.GENERATE_INVOICE, ORDER_ID))
                .thenReturn(RepairOrderStatus.WAITING_PAYMENT);
        when(repairTaskItemService.getRepairTaskItemById(TASK_ITEM_ID)).thenReturn(item);
        when(repairTaskItemStatusHistoryService.getAllTaskItemsStatusesByTaskId(TASK_ID))
                .thenReturn(List.of(RepairTaskItemStatus.READY));
        when(repairOrderService.getRepairOrderByTaskId(TASK_ID)).thenReturn(order);
        doAnswer(this::orchestratorExecute).when(self).execute(any(), anyLong());
        // Then
        orchestrator.execute(RepairTaskItemEvent.FINISH_WORK, TASK_ITEM_ID);

        verify(self, times(1)).execute(RepairTaskEvent.FINISH_TASK, TASK_ID);
        verify(self, times(1)).execute(RepairOrderEvent.GENERATE_INVOICE, ORDER_ID);
    }

    private Object orchestratorExecute(InvocationOnMock invocation) {
        WorkflowEvent<?> event = invocation.getArgument(0);
        Long id = invocation.getArgument(1);
        orchestrator.execute(event, id);
        return null;
    }
}

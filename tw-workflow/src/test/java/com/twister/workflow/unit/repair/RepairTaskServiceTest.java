package com.twister.workflow.unit.repair;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.twister.domain.reference.Transport;
import com.twister.domain.repair.RepairOrder;
import com.twister.domain.repair.RepairTask;
import com.twister.domain.repair.RepairTaskItem;
import com.twister.domain.repair.RepairTaskStatus;
import com.twister.workflow.dao.repair.RepairTaskDao;
import com.twister.workflow.service.repair.RepairOrderService;
import com.twister.workflow.service.repair.RepairTaskService;
import com.twister.workflow.service.repair.RepairTaskStatusHistoryService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RepairTaskServiceTest {

    private static final Long TASK_ID = 1L;
    private static final Long ORDER_ID = 10L;
    private static final Long NOT_EXISTING = Long.MAX_VALUE;

    @Mock
    private RepairTaskDao repairTaskDao;

    @Mock
    private RepairOrderService repairOrderService;

    @Mock
    private RepairTaskStatusHistoryService historyService;

    @InjectMocks
    private RepairTaskService taskService;

    @Captor
    private ArgumentCaptor<RepairTask> taskCaptor;

    @Captor
    private ArgumentCaptor<RepairTaskStatus> taskStatusCaptor;

    @Test
    void testGetRepairTaskByIdWhenIdExists() {
        // Given
        RepairTask task = RepairTask.builder().id(TASK_ID).build();
        // When
        when(repairTaskDao.findById(TASK_ID)).thenReturn(Optional.of(task));
        // Then
        RepairTask result = taskService.getRepairTaskById(TASK_ID);

        assertNotNull(result);
        assertEquals(TASK_ID, result.getId());

        verify(repairTaskDao, times(1)).findById(anyLong());
    }

    @Test
    void testGetRepairTaskByIdWhenIdNotExists() {
        // When
        when(repairTaskDao.findById(NOT_EXISTING)).thenReturn(Optional.empty());
        // Then
        assertThrows(NoSuchElementException.class, () -> taskService.getRepairTaskById(NOT_EXISTING));
    }

    @Test
    void testGetRepairTaskByIdWhenOrderIdExists() {
        // Given
        RepairTask task = RepairTask.builder().id(TASK_ID).build();
        // When
        when(repairTaskDao.findByOrderId(ORDER_ID)).thenReturn(Optional.of(task));
        // Then
        RepairTask result = taskService.getRepairTaskByOrderId(ORDER_ID);

        assertNotNull(result);
        assertEquals(TASK_ID, result.getId());

        verify(repairTaskDao, times(1)).findByOrderId(anyLong());
    }

    @Test
    void testGetRepairTaskByIdWhenOrderIdNotExists() {
        // When
        when(repairTaskDao.findByOrderId(NOT_EXISTING)).thenReturn(Optional.empty());
        // Then
        assertThrows(NoSuchElementException.class, () -> taskService.getRepairTaskByOrderId(NOT_EXISTING));
    }

    @Test
    void testCreateRepairTaskWhenAllDataValid() {
        // Given
        RepairOrder dbOrder = RepairOrder.builder()
                .id(ORDER_ID)
                .transport(Transport.builder().build())
                .build();
        RepairTask task = RepairTask.builder()
                .order(RepairOrder.builder().id(ORDER_ID).build())
                .build();
        // When
        when(repairOrderService.getRepairOrderById(ORDER_ID)).thenReturn(dbOrder);
        when(repairTaskDao.save(any(RepairTask.class))).thenAnswer(inv -> inv.getArgument(0));
        // Then
        taskService.create(task);

        verify(repairTaskDao, times(1)).save(taskCaptor.capture());
        verify(historyService, times(1)).updateWorkflowStatus(nullable(Long.class), taskStatusCaptor.capture());

        RepairTask result = taskCaptor.getValue();
        RepairTaskStatus resultStatus = taskStatusCaptor.getValue();

        assertEquals(dbOrder, result.getOrder());
        assertEquals(RepairTaskStatus.CREATED, resultStatus);
    }

    @Test
    void testCreateRepairTaskWhenDataIsNull() {
        // Then
        assertThrows(IllegalArgumentException.class, () -> taskService.create(null));
    }

    @Test
    void testCreateRepairTaskWhenOrderIsNull() {
        // Given
        RepairTask task = RepairTask.builder().build();
        // Then
        assertThrows(IllegalStateException.class, () -> taskService.create(task));
    }

    @Test
    void testCreateRepairTaskWhenItemsNotNull() {
        // Given
        RepairOrder order = RepairOrder.builder().id(ORDER_ID).build();
        RepairTask task = RepairTask.builder()
                .order(order)
                .items(List.of(RepairTaskItem.builder().build()))
                .build();
        // Then
        assertThrows(IllegalStateException.class, () -> taskService.create(task));
    }
}

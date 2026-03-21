package com.twister.workflow.unit.repair;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.twister.domain.repair.RepairOrder;
import com.twister.domain.repair.RepairOrderStatus;
import com.twister.domain.repair.RepairOrderStatusHistory;
import com.twister.workflow.dao.repair.StatusHistoryDao;
import com.twister.workflow.service.repair.RepairOrderService;
import com.twister.workflow.service.repair.RepairOrderStatusHistoryService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RepairOrderStatusHistoryServiceTest {

    private final Long ID = 1L;

    @Mock
    private StatusHistoryDao<RepairOrderStatusHistory> historyDao;

    @Mock
    private RepairOrderService repairOrderService;

    @Captor
    private ArgumentCaptor<RepairOrderStatusHistory> historyCaptor;

    private RepairOrderStatusHistoryService historyService;

    @BeforeEach
    void setUp() {
        historyService = new RepairOrderStatusHistoryService(repairOrderService);
        historyService.setStatusHistoryDao(historyDao);
    }

    @Test
    void testGetLastWorkflowStatusWhenWorkflowStatusExist() {
        // Given
        RepairOrderStatus status = RepairOrderStatus.ACCEPTED;
        RepairOrderStatusHistory history =
                RepairOrderStatusHistory.builder().status(status).build();
        // When
        when(historyDao.findFirstByWorkflowItemIdOrderByCreatedAtDesc(anyLong()))
                .thenReturn(Optional.of(history));
        // Then
        RepairOrderStatus result = historyService.getLastWorkflowStatus(ID);
        assertEquals(status, result);

        verify(historyDao, times(1)).findFirstByWorkflowItemIdOrderByCreatedAtDesc(anyLong());
    }

    @Test
    void testGetLastWorkflowStatusWhenWorkflowStatusNotExist() {
        // When
        when(historyDao.findFirstByWorkflowItemIdOrderByCreatedAtDesc(anyLong()))
                .thenReturn(Optional.empty());
        // Then
        assertThrows(NoSuchElementException.class, () -> historyService.getLastWorkflowStatus(ID));

        verify(historyDao, times(1)).findFirstByWorkflowItemIdOrderByCreatedAtDesc(anyLong());
    }

    @Test
    void testGetWorkflowStatusHistoryWhenWorkflowIdExist() {
        // Given
        RepairOrderStatusHistory history1 = RepairOrderStatusHistory.builder()
                .workflowItemId(ID)
                .status(RepairOrderStatus.ACCEPTED)
                .build();
        RepairOrderStatusHistory history2 = RepairOrderStatusHistory.builder()
                .workflowItemId(ID)
                .status(RepairOrderStatus.REJECTED)
                .build();
        // When
        when(historyDao.findByWorkflowItemId(ID)).thenReturn(List.of(history1, history2));
        // Then
        List<RepairOrderStatusHistory> result = historyService.getWorkflowStatusHistory(ID);

        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertEquals(RepairOrderStatus.ACCEPTED, result.getFirst().getStatus());
        assertEquals(RepairOrderStatus.REJECTED, result.getLast().getStatus());

        verify(historyDao, times(1)).findByWorkflowItemId(anyLong());
    }

    @Test
    void testGetWorkflowStatusHistoryWhenWorkflowIdNotExist() {
        // When
        when(historyDao.findByWorkflowItemId(anyLong())).thenReturn(List.of());
        // Then
        List<RepairOrderStatusHistory> result = historyService.getWorkflowStatusHistory(ID);

        assertTrue(result.isEmpty());

        verify(historyDao, times(1)).findByWorkflowItemId(anyLong());
    }

    @Test
    void testUpdateWorkflowStatusWhenStatusNotExist() {
        // Given
        RepairOrder order = RepairOrder.builder().id(ID).build();
        // When
        when(historyDao.findFirstByWorkflowItemIdOrderByCreatedAtDesc(ID)).thenReturn(Optional.empty());
        when(repairOrderService.getRepairOrderById(ID)).thenReturn(order);
        when(historyDao.save(any(RepairOrderStatusHistory.class))).thenAnswer(inv -> inv.getArgument(0));
        // Then
        historyService.updateWorkflowStatus(ID, RepairOrderStatus.ACCEPTED);

        verify(historyDao, times(1)).save(historyCaptor.capture());
        RepairOrderStatusHistory result = historyCaptor.getValue();

        assertEquals(RepairOrderStatus.ACCEPTED, result.getStatus());
        assertEquals(ID, result.getWorkflowItemId());

        verify(historyDao, times(1)).findFirstByWorkflowItemIdOrderByCreatedAtDesc(anyLong());
        verify(repairOrderService, times(1)).getRepairOrderById(anyLong());
        verify(historyDao, times(1)).save(any(RepairOrderStatusHistory.class));
    }

    @Test
    void testUpdateWorkflowStatusWhenStatusAlreadyExist() {
        // Given
        RepairOrderStatusHistory history = RepairOrderStatusHistory.builder()
                .id(ID)
                .workflowItemId(ID)
                .status(RepairOrderStatus.ACCEPTED)
                .build();
        // When
        when(historyDao.findFirstByWorkflowItemIdOrderByCreatedAtDesc(ID)).thenReturn(Optional.of(history));
        // Then
        RepairOrderStatus result = historyService.updateWorkflowStatus(ID, RepairOrderStatus.ACCEPTED);

        assertEquals(RepairOrderStatus.ACCEPTED, result);

        verify(historyDao, times(1)).findFirstByWorkflowItemIdOrderByCreatedAtDesc(anyLong());
        verify(repairOrderService, times(0)).getRepairOrderById(anyLong());
        verify(historyDao, times(0)).save(any(RepairOrderStatusHistory.class));
    }

    @Test
    void testValidateHistoryWhenWorkflowItemIdIsNull() {
        // Given
        RepairOrder order = RepairOrder.builder().build();
        // When
        when(historyDao.findFirstByWorkflowItemIdOrderByCreatedAtDesc(ID)).thenReturn(Optional.empty());
        when(repairOrderService.getRepairOrderById(ID)).thenReturn(order);
        // Then
        assertThrows(
                IllegalStateException.class, () -> historyService.updateWorkflowStatus(ID, RepairOrderStatus.ACCEPTED));

        verify(historyDao, times(1)).findFirstByWorkflowItemIdOrderByCreatedAtDesc(anyLong());
        verify(repairOrderService, times(1)).getRepairOrderById(anyLong());
        verify(historyDao, times(0)).save(any(RepairOrderStatusHistory.class));
    }

    @Test
    void testValidateHistoryWhenStatusIsNull() {
        // Given
        RepairOrder order = RepairOrder.builder().id(ID).build();
        // When
        when(historyDao.findFirstByWorkflowItemIdOrderByCreatedAtDesc(ID)).thenReturn(Optional.empty());
        when(repairOrderService.getRepairOrderById(ID)).thenReturn(order);
        // Then
        assertThrows(IllegalStateException.class, () -> historyService.updateWorkflowStatus(ID, null));

        verify(historyDao, times(1)).findFirstByWorkflowItemIdOrderByCreatedAtDesc(anyLong());
        verify(repairOrderService, times(1)).getRepairOrderById(anyLong());
        verify(historyDao, times(0)).save(any(RepairOrderStatusHistory.class));
    }
}

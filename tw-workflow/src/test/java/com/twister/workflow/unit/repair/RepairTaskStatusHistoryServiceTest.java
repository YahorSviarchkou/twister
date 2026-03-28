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

import com.twister.domain.repair.RepairTaskStatus;
import com.twister.domain.repair.RepairTaskStatusHistory;
import com.twister.workflow.dao.repair.StatusHistoryDao;
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
public class RepairTaskStatusHistoryServiceTest {

    private final Long ID = 1L;

    @Mock
    private StatusHistoryDao<RepairTaskStatusHistory> historyDao;

    @Captor
    private ArgumentCaptor<RepairTaskStatusHistory> historyCaptor;

    @InjectMocks
    private RepairTaskStatusHistoryService historyService;

    @Test
    void testGetLastWorkflowStatusWhenWorkflowStatusExist() {
        // Given
        RepairTaskStatus status = RepairTaskStatus.CREATED;
        RepairTaskStatusHistory history =
                RepairTaskStatusHistory.builder().status(status).build();
        // When
        when(historyDao.findFirstByWorkflowItemIdOrderByCreatedAtDesc(anyLong()))
                .thenReturn(Optional.of(history));
        // Then
        RepairTaskStatus result = historyService.getLastWorkflowStatus(ID);
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
        RepairTaskStatusHistory history1 = RepairTaskStatusHistory.builder()
                .workflowItemId(ID)
                .status(RepairTaskStatus.READY)
                .build();
        RepairTaskStatusHistory history2 = RepairTaskStatusHistory.builder()
                .workflowItemId(ID)
                .status(RepairTaskStatus.CANCELED)
                .build();
        // When
        when(historyDao.findByWorkflowItemId(ID)).thenReturn(List.of(history1, history2));
        // Then
        List<RepairTaskStatusHistory> result = historyService.getWorkflowStatusHistory(ID);

        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertEquals(RepairTaskStatus.READY, result.getFirst().getStatus());
        assertEquals(RepairTaskStatus.CANCELED, result.getLast().getStatus());

        verify(historyDao, times(1)).findByWorkflowItemId(anyLong());
    }

    @Test
    void testGetWorkflowStatusHistoryWhenWorkflowIdNotExist() {
        // When
        when(historyDao.findByWorkflowItemId(anyLong())).thenReturn(List.of());
        // Then
        List<RepairTaskStatusHistory> result = historyService.getWorkflowStatusHistory(ID);

        assertTrue(result.isEmpty());

        verify(historyDao, times(1)).findByWorkflowItemId(anyLong());
    }

    @Test
    void testUpdateWorkflowStatusWhenStatusNotExist() {
        // When
        when(historyDao.findFirstByWorkflowItemIdOrderByCreatedAtDesc(ID)).thenReturn(Optional.empty());
        when(historyDao.save(any(RepairTaskStatusHistory.class))).thenAnswer(inv -> inv.getArgument(0));
        // Then
        historyService.updateWorkflowStatus(ID, RepairTaskStatus.CREATED);

        verify(historyDao, times(1)).save(historyCaptor.capture());
        RepairTaskStatusHistory result = historyCaptor.getValue();

        assertEquals(RepairTaskStatus.CREATED, result.getStatus());
        assertEquals(ID, result.getWorkflowItemId());

        verify(historyDao, times(1)).findFirstByWorkflowItemIdOrderByCreatedAtDesc(anyLong());
        verify(historyDao, times(1)).save(any(RepairTaskStatusHistory.class));
    }

    @Test
    void testUpdateWorkflowStatusWhenStatusAlreadyExist() {
        // Given
        RepairTaskStatusHistory history = RepairTaskStatusHistory.builder()
                .id(ID)
                .workflowItemId(ID)
                .status(RepairTaskStatus.CREATED)
                .build();
        // When
        when(historyDao.findFirstByWorkflowItemIdOrderByCreatedAtDesc(ID)).thenReturn(Optional.of(history));
        // Then
        RepairTaskStatus result = historyService.updateWorkflowStatus(ID, RepairTaskStatus.CREATED);

        assertEquals(RepairTaskStatus.CREATED, result);

        verify(historyDao, times(1)).findFirstByWorkflowItemIdOrderByCreatedAtDesc(anyLong());
        verify(historyDao, times(0)).save(any(RepairTaskStatusHistory.class));
    }

    @Test
    void testValidateHistoryWhenWorkflowItemIdIsNull() {
        // When
        when(historyDao.findFirstByWorkflowItemIdOrderByCreatedAtDesc(null)).thenReturn(Optional.empty());
        // Then
        assertThrows(
                IllegalStateException.class, () -> historyService.updateWorkflowStatus(null, RepairTaskStatus.CREATED));

        verify(historyDao, times(1)).findFirstByWorkflowItemIdOrderByCreatedAtDesc(null);
        verify(historyDao, times(0)).save(any(RepairTaskStatusHistory.class));
    }

    @Test
    void testValidateHistoryWhenStatusIsNull() {
        // When
        when(historyDao.findFirstByWorkflowItemIdOrderByCreatedAtDesc(ID)).thenReturn(Optional.empty());
        // Then
        assertThrows(IllegalStateException.class, () -> historyService.updateWorkflowStatus(ID, null));

        verify(historyDao, times(1)).findFirstByWorkflowItemIdOrderByCreatedAtDesc(anyLong());
        verify(historyDao, times(0)).save(any(RepairTaskStatusHistory.class));
    }
}

package com.twister.workflow.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.twister.domain.repair.RepairOrderStatus;
import com.twister.domain.repair.RepairOrderStatusHistory;
import com.twister.workflow.dao.repair.RepairOrderDao;
import com.twister.workflow.dao.repair.StatusHistoryDao;
import com.twister.workflow.service.workflow.WorkflowEventHandler;
import com.twister.workflow.service.workflow.event.RepairOrderEvent;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;

public class WorkflowEventHandlerTest extends BaseIntegrationTest {

    private static final Long ID = 1L;
    private static final Long NOT_EXISTING = 999L;

    @Autowired
    private WorkflowEventHandler handler;

    @Autowired
    private RepairOrderDao repairOrderDaoMock;

    @Autowired
    private StatusHistoryDao<RepairOrderStatusHistory> repairOrderStatusHistoryDaoMock;

    @Captor
    private ArgumentCaptor<RepairOrderStatusHistory> historyCaptor;

    @Test
    void testApplyEventWhenAllDataExists() {
        // Given
        RepairOrderStatusHistory history = RepairOrderStatusHistory.builder()
                .id(ID)
                .workflowItemId(ID)
                .status(RepairOrderStatus.OPEN)
                .build();
        // When
        when(repairOrderDaoMock.existsById(ID)).thenReturn(true);
        when(repairOrderStatusHistoryDaoMock.findFirstByWorkflowItemIdOrderByCreatedAtDesc(ID))
                .thenReturn(Optional.of(history));
        when(repairOrderStatusHistoryDaoMock.save(any(RepairOrderStatusHistory.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        // Then
        handler.applyEvent(RepairOrderEvent.ACCEPT_ORDER, ID);

        verify(repairOrderStatusHistoryDaoMock, times(1)).save(historyCaptor.capture());
        RepairOrderStatusHistory result = historyCaptor.getValue();

        assertEquals(RepairOrderStatus.ACCEPTED, result.getStatus());
    }

    @Test
    void testApplyEventWhenEventIsNull() {
        assertThrows(IllegalArgumentException.class, () -> handler.applyEvent(null, ID));
    }

    @Test
    void testApplyEventWhenWorkflowItemNotFound() {
        // When
        when(repairOrderDaoMock.existsById(NOT_EXISTING)).thenReturn(false);
        // Then
        assertThrows(
                IllegalArgumentException.class, () -> handler.applyEvent(RepairOrderEvent.ACCEPT_ORDER, NOT_EXISTING));
    }
}

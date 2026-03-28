package com.twister.workflow.unit.repair;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.twister.domain.reference.ServiceType;
import com.twister.domain.reference.Spare;
import com.twister.domain.repair.RepairOrder;
import com.twister.domain.repair.RepairTask;
import com.twister.domain.repair.RepairTaskItem;
import com.twister.domain.repair.RepairTaskItemStatus;
import com.twister.workflow.dao.repair.RepairTaskItemDao;
import com.twister.workflow.service.reference.ServiceTypeService;
import com.twister.workflow.service.reference.SpareService;
import com.twister.workflow.service.repair.RepairTaskItemService;
import com.twister.workflow.service.repair.RepairTaskItemStatusHistoryService;
import com.twister.workflow.service.repair.RepairTaskService;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RepairTaskItemServiceTest {

    private static final Long ITEM_ID = 1L;
    private static final Long TASK_ID = 10L;
    private static final Long SERVICE_TYPE_ID = 100L;
    private static final Long SPARE_ID_1 = 200L;
    private static final Long SPARE_ID_2 = 201L;

    @Mock
    private RepairTaskItemDao repairTaskItemDao;

    @Mock
    private RepairTaskService repairTaskService;

    @Mock
    private RepairTaskItemStatusHistoryService historyService;

    @Mock
    private ServiceTypeService serviceTypeService;

    @Mock
    private SpareService spareService;

    @InjectMocks
    private RepairTaskItemService taskItemService;

    @Captor
    private ArgumentCaptor<RepairTaskItem> itemCaptor;

    @Captor
    private ArgumentCaptor<RepairTaskItemStatus> itemStatusCaptor;

    @Test
    void testCreateTaskItemWhenAllDataValid() {
        // Given
        ServiceType dbServiceType =
                ServiceType.builder().id(SERVICE_TYPE_ID).name("SERVICE_NAME").build();
        Spare dbSpare = Spare.builder().id(SPARE_ID_1).name("SPARE_NAME").build();
        RepairTask dbTask = RepairTask.builder()
                .id(TASK_ID)
                .order(RepairOrder.builder().build())
                .build();
        RepairTaskItem item = RepairTaskItem.builder()
                .task(RepairTask.builder().id(TASK_ID).build())
                .serviceType(ServiceType.builder().id(SERVICE_TYPE_ID).build())
                .spares(List.of(Spare.builder().id(SPARE_ID_1).build()))
                .build();
        // When
        when(repairTaskService.getRepairTaskById(TASK_ID)).thenReturn(dbTask);
        when(serviceTypeService.getById(SERVICE_TYPE_ID)).thenReturn(dbServiceType);
        when(spareService.getAllByIds(Set.of(SPARE_ID_1))).thenReturn(List.of(dbSpare));
        when(repairTaskItemDao.save(any(RepairTaskItem.class))).thenAnswer(inv -> inv.getArgument(0));
        // Then
        taskItemService.create(item);

        verify(repairTaskItemDao, times(1)).save(itemCaptor.capture());
        verify(historyService, times(1)).updateWorkflowStatus(nullable(Long.class), itemStatusCaptor.capture());

        RepairTaskItem result = itemCaptor.getValue();
        RepairTaskItemStatus statusResult = itemStatusCaptor.getValue();

        assertEquals(dbTask, result.getTask());
        assertEquals(1, result.getSpares().size());
        assertEquals(dbSpare, result.getSpares().getFirst());
        assertEquals(dbServiceType, result.getServiceType());
        assertEquals(RepairTaskItemStatus.CREATED, statusResult);
    }

    @Test
    void testCreateTaskItemWhenDataIsNull() {
        // Then
        assertThrows(IllegalArgumentException.class, () -> taskItemService.create(null));
    }

    @Test
    void testCreateTaskItemWhenTaskIsNull() {
        // Given
        RepairTaskItem item = RepairTaskItem.builder().build();
        // Then
        assertThrows(IllegalStateException.class, () -> taskItemService.create(item));
    }

    @Test
    void testUpdateSparesToTaskItemWhenAllDataValid() {
        // Given
        Spare dbSpare2 = Spare.builder().id(SPARE_ID_2).build();
        RepairTaskItem item = RepairTaskItem.builder()
                .id(ITEM_ID)
                .spares(List.of(Spare.builder().id(SPARE_ID_1).build()))
                .build();
        // When
        when(repairTaskItemDao.findById(ITEM_ID)).thenReturn(Optional.of(item));
        when(spareService.getAllByIds(Set.of(SPARE_ID_2))).thenReturn(List.of(dbSpare2));
        // Then
        taskItemService.updateSpares(Set.of(SPARE_ID_2), ITEM_ID);

        verify(repairTaskItemDao, times(1)).save(itemCaptor.capture());
        RepairTaskItem result = itemCaptor.getValue();

        assertEquals(1, result.getSpares().size());
        assertEquals(dbSpare2, result.getSpares().getFirst());
    }

    @Test
    void testUpdateSparesToTaskItemWhenSparesIsNull() {
        // Given
        RepairTaskItem item = RepairTaskItem.builder()
                .id(ITEM_ID)
                .spares(List.of(Spare.builder().id(SPARE_ID_1).build()))
                .build();
        // When
        when(repairTaskItemDao.findById(ITEM_ID)).thenReturn(Optional.of(item));
        // Then
        taskItemService.updateSpares(null, ITEM_ID);

        verify(repairTaskItemDao, times(1)).save(itemCaptor.capture());
        RepairTaskItem result = itemCaptor.getValue();

        assertTrue(result.getSpares().isEmpty());
    }

    @Test
    void testUpdateSparesToTaskItemWhenSparesNotExist() {
        // Given
        Spare spare = Spare.builder().id(SPARE_ID_1).build();
        RepairTaskItem item =
                RepairTaskItem.builder().id(ITEM_ID).spares(List.of(spare)).build();
        // When
        when(repairTaskItemDao.findById(ITEM_ID)).thenReturn(Optional.of(item));
        when(spareService.getAllByIds(Set.of(SPARE_ID_2))).thenReturn(List.of());
        // Then
        taskItemService.updateSpares(Set.of(SPARE_ID_2), ITEM_ID);

        verify(repairTaskItemDao, times(0)).save(any(RepairTaskItem.class));
    }

    @Test
    void testUpdateServiceTypeForTaskItemWhenAllDataValid() {
        // Given
        ServiceType dbServiceType =
                ServiceType.builder().id(SERVICE_TYPE_ID).name("SERVICE_NAME").build();
        RepairTaskItem item = RepairTaskItem.builder().id(ITEM_ID).build();
        // When
        when(repairTaskItemDao.findById(ITEM_ID)).thenReturn(Optional.of(item));
        when(serviceTypeService.getById(SERVICE_TYPE_ID)).thenReturn(dbServiceType);
        // Then
        taskItemService.updateServiceType(SERVICE_TYPE_ID, ITEM_ID);

        verify(repairTaskItemDao, times(1)).save(itemCaptor.capture());
        RepairTaskItem result = itemCaptor.getValue();

        assertEquals(dbServiceType, result.getServiceType());
    }

    @Test
    void testUpdateServiceTypeForTaskItemWhenServiceTypeIsNull() {
        // Then
        assertThrows(IllegalArgumentException.class, () -> taskItemService.updateServiceType(null, ITEM_ID));
    }
}

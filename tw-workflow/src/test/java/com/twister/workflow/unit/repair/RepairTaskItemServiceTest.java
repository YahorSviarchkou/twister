package com.twister.workflow.unit.repair;

import com.twister.workflow.dao.repair.RepairTaskItemDao;
import com.twister.workflow.service.reference.ServiceTypeService;
import com.twister.workflow.service.reference.SpareService;
import com.twister.workflow.service.repair.RepairTaskItemService;
import com.twister.workflow.service.repair.RepairTaskItemStatusHistoryService;
import com.twister.workflow.service.repair.RepairTaskService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RepairTaskItemServiceTest {

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
}

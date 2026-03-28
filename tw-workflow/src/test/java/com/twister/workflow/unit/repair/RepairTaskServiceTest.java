package com.twister.workflow.unit.repair;

import com.twister.workflow.dao.repair.RepairTaskDao;
import com.twister.workflow.service.repair.RepairOrderService;
import com.twister.workflow.service.repair.RepairTaskService;
import com.twister.workflow.service.repair.RepairTaskStatusHistoryService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RepairTaskServiceTest {

    @Mock
    private RepairTaskDao repairTaskDao;

    @Mock
    private RepairOrderService repairOrderService;

    @Mock
    private RepairTaskStatusHistoryService historyService;

    @InjectMocks
    private RepairTaskService taskService;
}

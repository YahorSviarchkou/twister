package com.twister.workflow.unit.repair;

import com.twister.workflow.dao.repair.RepairInvoiceDao;
import com.twister.workflow.service.repair.RepairInvoiceService;
import com.twister.workflow.service.repair.RepairOrderService;
import com.twister.workflow.service.repair.RepairTaskItemService;
import com.twister.workflow.service.repair.RepairTaskService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RepairInvoiceServiceTest {

    @Mock
    private RepairInvoiceDao invoiceDao;

    @Mock
    private RepairOrderService repairOrderService;

    @Mock
    private RepairTaskService repairTaskService;

    @Mock
    private RepairTaskItemService repairTaskItemService;

    @InjectMocks
    private RepairInvoiceService invoiceService;
}

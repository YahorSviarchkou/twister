package com.twister.workflow.unit.repair;

import com.twister.workflow.dao.repair.RepairOrderDao;
import com.twister.workflow.service.CustomerService;
import com.twister.workflow.service.reference.TransportService;
import com.twister.workflow.service.repair.RepairOrderService;
import com.twister.workflow.service.repair.RepairOrderStatusHistoryService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RepairOrderServiceTest {

    @Mock
    private RepairOrderDao repairOrderDao;

    @Mock
    private CustomerService customerService;

    @Mock
    private TransportService transportService;

    @Mock
    private RepairOrderStatusHistoryService historyService;

    @InjectMocks
    private RepairOrderService orderService;
}

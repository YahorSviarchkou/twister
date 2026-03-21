package com.twister.workflow.service.repair;

import com.twister.domain.repair.RepairOrder;
import com.twister.domain.repair.RepairOrderStatus;
import com.twister.domain.repair.RepairOrderStatusHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RepairOrderStatusHistoryService
        extends StatusHistoryService<RepairOrder, RepairOrderStatusHistory, RepairOrderStatus> {

    private final RepairOrderService repairOrderService;

    @Override
    public Class<RepairOrder> getWorkflowClass() {
        return RepairOrder.class;
    }

    @Override
    protected RepairOrderStatusHistory buildHistory(Long workflowItemId, RepairOrderStatus status) {
        RepairOrder repairOrder = repairOrderService.getRepairOrderById(workflowItemId);
        return RepairOrderStatusHistory.builder()
                .workflowItemId(repairOrder.getId())
                .status(status)
                .build();
    }
}

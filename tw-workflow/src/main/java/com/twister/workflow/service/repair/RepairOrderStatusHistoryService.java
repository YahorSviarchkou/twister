package com.twister.workflow.service.repair;

import com.twister.domain.repair.RepairOrder;
import com.twister.domain.repair.RepairOrderStatus;
import com.twister.domain.repair.RepairOrderStatusHistory;
import com.twister.workflow.dao.repair.StatusHistoryDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RepairOrderStatusHistoryService
        extends StatusHistoryService<RepairOrder, RepairOrderStatusHistory, RepairOrderStatus> {

    private final StatusHistoryDao<RepairOrderStatusHistory> repairOrderStatusHistoryDao;

    @Override
    public Class<RepairOrder> getWorkflowClass() {
        return RepairOrder.class;
    }

    @Override
    protected StatusHistoryDao<RepairOrderStatusHistory> getStatusHistoryDao() {
        return repairOrderStatusHistoryDao;
    }

    @Override
    protected RepairOrderStatusHistory buildHistory(Long workflowItemId, RepairOrderStatus status) {
        return RepairOrderStatusHistory.builder()
                .workflowItemId(workflowItemId)
                .status(status)
                .build();
    }
}

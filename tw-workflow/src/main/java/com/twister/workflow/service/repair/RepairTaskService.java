package com.twister.workflow.service.repair;

import com.twister.domain.repair.RepairOrder;
import com.twister.domain.repair.RepairTask;
import com.twister.domain.repair.RepairTaskStatus;
import com.twister.workflow.dao.repair.RepairTaskDao;
import com.twister.workflow.service.workflow.WorkflowService;
import com.twister.workflow.service.workflow.event.RepairTaskEvent;
import com.twister.workflow.service.workflow.event.WorkflowEvent;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RepairTaskService implements WorkflowService<RepairTask> {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private final RepairTaskDao repairTaskDao;

    private final RepairOrderService repairOrderService;
    private final RepairTaskStatusHistoryService historyService;

    @Override
    public Class<? extends WorkflowEvent<RepairTask>> getWorkflowEvent() {
        return RepairTaskEvent.class;
    }

    @Override
    public boolean existsById(Long id) {
        return repairTaskDao.existsById(id);
    }

    public RepairTask getRepairTaskById(Long id) {
        return repairTaskDao
                .findById(id)
                .orElseThrow(() -> new NoSuchElementException("RepairTask not found by id: " + id));
    }

    public RepairTask getRepairTaskByOrderId(Long orderId) {
        return repairTaskDao
                .findByOrderId(orderId)
                .orElseThrow(() -> new NoSuchElementException("RepairTask not found by orderId: " + orderId));
    }

    public Page<RepairTask> getAllRepairTasks(Pageable pageable) {
        return repairTaskDao.findAll(pageable);
    }

    @Transactional
    public void create(RepairTask repairTask) {
        if (repairTask == null) {
            throw new IllegalArgumentException("RepairTask not exist for creating");
        }
        if (repairTask.getItems() != null && !repairTask.getItems().isEmpty()) {
            throw new IllegalStateException("RepairTask must not contains items on creating");
        }

        log.info("Creating repair task");
        if (repairTask.getOrder() == null) {
            throw new IllegalStateException("RepairTask must related with repair order");
        }
        RepairOrder repairOrder =
                repairOrderService.getRepairOrderById(repairTask.getOrder().getId());

        repairTask.setId(null);
        repairTask.setOrder(repairOrder);

        RepairTask dbRepairTask = repairTaskDao.save(repairTask);
        log.info("RepairTask successfully created with id: {}", dbRepairTask.getId());

        historyService.updateWorkflowStatus(dbRepairTask.getId(), RepairTaskStatus.CREATED);
    }
}

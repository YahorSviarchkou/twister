package com.twister.service.repair;

import com.twister.domain.repair.RepairOrder;
import com.twister.domain.repair.RepairTask;
import com.twister.domain.repair.RepairTaskStatus;
import com.twister.repository.repair.RepairTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class RepairTaskService {

    private final RepairTaskRepository repairTaskRepository;
    private final RepairOrderService repairOrderService;
    private final RepairTaskStatusHistoryService historyService;

    public RepairTask getRepairTaskById(Long id) {
        return repairTaskRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("RepairTask not found by id: " + id));
    }

    public RepairTask getRepairTaskByOrderId(Long orderId) {
        return repairTaskRepository.findByOrder_Id(orderId)
            .orElseThrow(() -> new NoSuchElementException("RepairTask not found by orderId: " + orderId));
    }

    public Page<RepairTask> getAllRepairTasks(Pageable pageable) {
        return repairTaskRepository.findAll(pageable);
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
        RepairOrder repairOrder = repairOrderService.getRepairOrderById(repairTask.getOrder().getId());

        repairTask.setId(null);
        repairTask.setOrder(repairOrder);

        RepairTask dbRepairTask = repairTaskRepository.save(repairTask);
        log.info("RepairTask successfully created with id: {}", dbRepairTask.getId());

        historyService.updateWorkflowStatus(dbRepairTask.getId(), RepairTaskStatus.CREATED);
    }

    public void update(RepairTask repairTask) {
        if (repairTask == null || repairTask.getId() == null) {
            throw new IllegalArgumentException("RepairTask not exist for updating");
        }
        if (repairTask.getItems() != null && !repairTask.getItems().isEmpty()) {
            throw new IllegalStateException("RepairTask must not contains items on updating");
        }

    }
}

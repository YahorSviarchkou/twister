package com.twister.workflow.service.repair;

import com.twister.domain.reference.Reference;
import com.twister.domain.reference.ServiceType;
import com.twister.domain.reference.Spare;
import com.twister.domain.repair.RepairTask;
import com.twister.domain.repair.RepairTaskItem;
import com.twister.domain.repair.RepairTaskItemStatus;
import com.twister.workflow.dao.repair.RepairTaskItemDao;
import com.twister.workflow.service.reference.ServiceTypeService;
import com.twister.workflow.service.reference.SpareService;
import com.twister.workflow.service.workflow.WorkflowService;
import com.twister.workflow.service.workflow.event.RepairTaskItemEvent;
import com.twister.workflow.service.workflow.event.WorkflowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RepairTaskItemService implements WorkflowService<RepairTaskItem> {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private final RepairTaskItemDao repairTaskItemDao;

    private final RepairTaskService repairTaskService;
    private final RepairTaskItemStatusHistoryService historyService;
    private final ServiceTypeService serviceTypeService;
    private final SpareService spareService;

    @Override
    public Class<? extends WorkflowEvent<RepairTaskItem>> getWorkflowEvent() {
        return RepairTaskItemEvent.class;
    }

    @Override
    public boolean existsById(Long id) {
        return repairTaskItemDao.existsById(id);
    }

    public RepairTaskItem getRepairTaskItemById(Long id) {
        return repairTaskItemDao
                .findById(id)
                .orElseThrow(() -> new NoSuchElementException("RepairTaskItem not found by id: " + id));
    }

    public List<RepairTaskItem> getAllByTaskId(Long taskId) {
        return repairTaskItemDao.findRepairTaskItemByTaskId(taskId);
    }

    @Transactional
    public void create(RepairTaskItem repairTaskItem) {
        if (repairTaskItem == null) {
            throw new IllegalArgumentException("RepairTaskItem not exist for creating");
        }

        log.info("Creating repair task item");
        if (repairTaskItem.getTask() == null) {
            throw new IllegalStateException("RepairTaskItem must related with repair task");
        }
        RepairTask repairTask =
                repairTaskService.getRepairTaskById(repairTaskItem.getTask().getId());

        repairTaskItem.setId(null);
        repairTaskItem.setTask(repairTask);

        Optional.ofNullable(repairTaskItem.getServiceType())
                .map(serviceType -> serviceTypeService.getById(serviceType.getId()))
                .ifPresent(repairTaskItem::setServiceType);

        Optional.ofNullable(repairTaskItem.getSpares())
                .map(spares -> spares.stream().map(Reference::getId).collect(Collectors.toSet()))
                .map(spareService::getAllByIds)
                .ifPresent(repairTaskItem::setSpares);

        RepairTaskItem dbRepairTaskItem = repairTaskItemDao.save(repairTaskItem);
        log.info("RepairTaskItem successfully created with id: {}", dbRepairTaskItem.getId());

        historyService.updateWorkflowStatus(dbRepairTaskItem.getId(), RepairTaskItemStatus.CREATED);
    }

    public void updateSpares(Set<Long> sparesIds, Long repairTaskItemId) {
        RepairTaskItem dbRepairTaskItem = getRepairTaskItemById(repairTaskItemId);

        List<Spare> updatedSpares;
        if (sparesIds == null || sparesIds.isEmpty()) {
            log.info("Removing all spares from task item with id: {}", repairTaskItemId);
            updatedSpares = new ArrayList<>();
        } else {
            updatedSpares = spareService.getAllByIds(sparesIds);
            if (updatedSpares.isEmpty()) {
                log.warn("No spares found to update repair task item id: {}", repairTaskItemId);
                return;
            }
        }

        RepairTaskItem updatedItem =
                dbRepairTaskItem.toBuilder().spares(updatedSpares).build();
        repairTaskItemDao.save(updatedItem);
        log.info("Spares successfully added to RepairTaskItem with id: {}", updatedItem.getId());
    }

    public void updateServiceType(Long serviceTypeId, Long repairTaskItemId) {
        if (serviceTypeId == null) {
            throw new IllegalArgumentException("ServiceType not exist for updating repair task item");
        }

        RepairTaskItem dbRepairTaskItem = getRepairTaskItemById(repairTaskItemId);

        ServiceType previousServiceType = dbRepairTaskItem.getServiceType();
        ServiceType dbServiceType = serviceTypeService.getById(serviceTypeId);

        dbRepairTaskItem.setServiceType(dbServiceType);
        repairTaskItemDao.save(dbRepairTaskItem);

        log.info(
                "ServiceType changed: {} -> {}, for RepairTaskItem with id: {}",
                Optional.ofNullable(previousServiceType).map(Reference::getName).orElse(null),
                dbServiceType.getName(),
                dbRepairTaskItem.getId());
    }
}

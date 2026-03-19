package com.twister.workflow.service.repair;

import com.twister.domain.reference.Reference;
import com.twister.domain.reference.ServiceType;
import com.twister.domain.reference.Spare;
import com.twister.domain.repair.RepairTask;
import com.twister.domain.repair.RepairTaskItem;
import com.twister.domain.repair.RepairTaskItemStatus;
import com.twister.repository.repair.RepairTaskItemRepository;
import com.twister.workflow.service.reference.ServiceTypeService;
import com.twister.workflow.service.reference.SpareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RepairTaskItemService {

    private final RepairTaskItemRepository repository;
    private final RepairTaskService repairTaskService;
    private final RepairTaskItemStatusHistoryService historyService;
    private final ServiceTypeService serviceTypeService;
    private final SpareService spareService;

    public List<RepairTaskItem> getAllByTaskId(Long taskId) {
        return repository.findRepairTaskItemByTask_Id(taskId);
    }

    public RepairTaskItem getRepairTaskItemById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("RepairTaskItem not found by id: " + id));
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
        RepairTask repairTask = repairTaskService.getRepairTaskById(repairTaskItem.getTask().getId());

        repairTaskItem.setId(null);
        repairTaskItem.setTask(repairTask);

        Optional.ofNullable(repairTaskItem.getServiceType())
            .map(serviceType -> serviceTypeService.getById(serviceType.getId()))
            .ifPresent(repairTaskItem::setServiceType);

        Optional.ofNullable(repairTaskItem.getSpares())
            .map(spares -> spares.stream()
                .map(Reference::getId)
                .collect(Collectors.toSet())
            )
            .map(spareService::getAllByIds)
            .ifPresent(repairTaskItem::setSpares);

        RepairTaskItem dbRepairTaskItem = repository.save(repairTaskItem);
        log.info("RepairTaskItem successfully created with id: {}", dbRepairTaskItem.getId());

        historyService.updateWorkflowStatus(dbRepairTaskItem.getId(), RepairTaskItemStatus.CREATED);
    }

    public void addSpares(Set<Long> sparesIds, Long repairTaskItemId) {
        if (sparesIds == null || sparesIds.isEmpty()) {
            throw new IllegalArgumentException("Spares not exist for updating repair task item");
        }

        RepairTaskItem dbRepairTaskItem = getRepairTaskItemById(repairTaskItemId);

        List<Spare> dbSpares = spareService.getAllByIds(sparesIds);
        if (dbSpares.isEmpty()) {
            log.warn("No spares found to update repair task item id: {}", repairTaskItemId);
            return;
        }

        dbRepairTaskItem.getSpares().addAll(dbSpares);
        repository.save(dbRepairTaskItem);
        log.info("Spares successfully added to RepairTaskItem with id: {}", dbRepairTaskItem.getId());
    }

    public void updateServiceType(Long serviceTypeId, Long repairTaskItemId) {
        if (serviceTypeId == null) {
            throw new IllegalArgumentException("ServiceType not exist for updating repair task item");
        }

        RepairTaskItem dbRepairTaskItem = getRepairTaskItemById(repairTaskItemId);

        ServiceType previousServiceType = dbRepairTaskItem.getServiceType();
        ServiceType dbServiceType = serviceTypeService.getById(serviceTypeId);

        dbRepairTaskItem.setServiceType(dbServiceType);
        repository.save(dbRepairTaskItem);

        log.info("ServiceType changed: {} -> {}, for RepairTaskItem with id: {}",
            previousServiceType.getName(), dbServiceType.getName(), dbRepairTaskItem.getId());
    }
}

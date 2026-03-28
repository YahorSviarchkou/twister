package com.twister.workflow.service.workflow;

import com.twister.domain.repair.RepairOrder;
import com.twister.domain.repair.RepairOrderStatus;
import com.twister.domain.repair.RepairTask;
import com.twister.domain.repair.RepairTaskItem;
import com.twister.domain.repair.RepairTaskItemStatus;
import com.twister.domain.repair.RepairTaskItemStatusHistory;
import com.twister.domain.repair.RepairTaskStatus;
import com.twister.workflow.service.repair.RepairOrderService;
import com.twister.workflow.service.repair.RepairTaskItemService;
import com.twister.workflow.service.repair.RepairTaskItemStatusHistoryService;
import com.twister.workflow.service.repair.RepairTaskService;
import com.twister.workflow.service.workflow.event.RepairOrderEvent;
import com.twister.workflow.service.workflow.event.RepairTaskEvent;
import com.twister.workflow.service.workflow.event.RepairTaskItemEvent;
import com.twister.workflow.service.workflow.event.WorkflowEvent;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowOrchestrator {

    private final WorkflowEventHandler eventHandler;
    private final RepairOrderService repairOrderService;
    private final RepairTaskService repairTaskService;
    private final RepairTaskItemService repairTaskItemService;
    private final RepairTaskItemStatusHistoryService repairTaskItemStatusHistoryService;

    @Lazy
    @Autowired
    @SuppressWarnings("all")
    private WorkflowOrchestrator self;

    @Transactional
    public <E extends WorkflowEvent<?>> void execute(E event, Long workflowItemId) {
        if (event == null || workflowItemId == null) {
            throw new IllegalArgumentException("Event and workflowItemId must exist");
        }

        Object resultingStatus = eventHandler.applyEvent(event, workflowItemId);

        switch (event) {
            case RepairOrderEvent ignored -> handleCascadeForOrder((RepairOrderStatus) resultingStatus, workflowItemId);
            case RepairTaskEvent ignored -> handleCascadeForTask((RepairTaskStatus) resultingStatus, workflowItemId);
            case RepairTaskItemEvent ignored -> handleCascadeForTaskItem(
                    (RepairTaskItemStatus) resultingStatus, workflowItemId);
            default -> throw new UnsupportedOperationException("Unsupported event: " + event);
        }
    }

    private void handleCascadeForOrder(RepairOrderStatus status, Long orderId) {
        switch (status) {
            case ACCEPTED -> handleAcceptedOrder(orderId);
            case REJECTED -> handleRejectedOrder(orderId);
        }
    }

    private void handleCascadeForTask(RepairTaskStatus status, Long taskId) {
        switch (status) {
            case READY -> handleReadyTask(taskId);
            case CANCELED -> handleCanceledTask(taskId);
        }
    }

    private void handleCascadeForTaskItem(RepairTaskItemStatus status, Long taskItemId) {
        if (status == RepairTaskItemStatus.READY) {
            handleReadyTaskItem(taskItemId);
        }
    }

    private void handleAcceptedOrder(Long orderId) {
        RepairOrder repairOrder = repairOrderService.getRepairOrderById(orderId);

        RepairTask repairTask = new RepairTask();
        repairTask.setOrder(repairOrder);

        repairTaskService.create(repairTask);
    }

    private void handleRejectedOrder(Long orderId) {
        RepairTask repairTask = repairTaskService.getRepairTaskByOrderId(orderId);
        self.execute(RepairTaskEvent.CANCEL, repairTask.getId());
    }

    private void handleReadyTask(Long taskId) {
        RepairOrder repairOrder = repairOrderService.getRepairOrderByTaskId(taskId);
        self.execute(RepairOrderEvent.GENERATE_INVOICE, repairOrder.getId());
        // todo gen invoice needed? or manual
    }

    private void handleCanceledTask(Long taskId) {
        List<RepairTaskItem> repairTaskItems = repairTaskItemService.getAllByTaskId(taskId);
        repairTaskItems.forEach(item -> self.execute(RepairTaskItemEvent.CANCEL, item.getId()));
    }

    private void handleReadyTaskItem(Long taskItemId) {
        RepairTaskItem item = repairTaskItemService.getRepairTaskItemById(taskItemId);
        long taskId = item.getTask().getId();

        List<RepairTaskItemStatusHistory> itemStatuses =
                repairTaskItemStatusHistoryService.getLastWorkflowStatusesByTaskId(taskId);

        boolean allItemsReady = itemStatuses.stream()
                .map(RepairTaskItemStatusHistory::getStatus)
                .allMatch(status -> status == RepairTaskItemStatus.READY);

        if (allItemsReady) {
            self.execute(RepairTaskEvent.FINISH_TASK, taskId);
        }
    }
}

package com.twister.service.repair;

import com.twister.domain.repair.StatusHistoryEntity;
import com.twister.repository.repair.StatusHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
public abstract class StatusHistoryService<W, H extends StatusHistoryEntity<W, S>, S> {

    private StatusHistoryRepository<H> repository;

    public abstract Class<W> getWorkflowClass();

    protected abstract Class<H> getStatusHistoryClass();

    protected abstract H buildHistory(Long workflowItem, S status);

    public void setRepository(@Autowired StatusHistoryRepository<H> repository) {
        this.repository = repository;
    }

    public Optional<H> getLastWorkflowStatusHistory(Long workflowItemId) {
        return repository.findFirstByWorkflowItem_IdOrderByCreatedAtDesc(workflowItemId);
    }

    public S getLastWorkflowStatus(Long workflowItemId) {
        Optional<H> lastWorkflowStatusHistory = getLastWorkflowStatusHistory(workflowItemId);
        return lastWorkflowStatusHistory
            .map(StatusHistoryEntity::getStatus)
            .orElseThrow(() -> new NoSuchElementException(
                    "Status history not found for workflow %s by workflow item id: %s"
                        .formatted(getWorkflowClassName(), workflowItemId)
                )
            );
    }

    public List<H> getWorkflowStatusHistory(Long workflowItemId) {
        List<H> statusHistory = repository.findByWorkflowItem_Id(workflowItemId);
        return statusHistory.isEmpty()
            ? Collections.emptyList()
            : Collections.unmodifiableList(statusHistory);
    }

    public S updateWorkflowStatus(Long workflowItemId, S status) {
        String workflow = getWorkflowClassName();
        log.info("Updating {} workflow item id: {}, with status: {}", workflow, workflowItemId, status);

        Optional<H> lastHistory = getLastWorkflowStatusHistory(workflowItemId);

        if (lastHistory.isPresent() && lastHistory.get().getStatus() == status) {
            log.warn("Status {} already defined for {} workflow item with id: {}", status, workflow, workflowItemId);
            return status;
        }

        H history = buildHistory(workflowItemId, status);
        validateHistory(history, workflowItemId, status);

        H dbHistory = repository.save(history);
        log.info(
            "Status: {}, successfully applied to {} workflow item id: {}, historyId: {}",
            status, workflow, workflowItemId, dbHistory.getId()
        );
        return status;
    }

    private void validateHistory(H history, Long workflowItemId, S status) {
        if (history.getWorkflowItem() == null || history.getStatus() == null) {
            throw new IllegalStateException(
                "History fields are not defined for workflowItemId: %s, status: %s"
                    .formatted(workflowItemId, status)
            );
        }
    }

    private String getWorkflowClassName() {
        return getWorkflowClass().getSimpleName();
    }
}

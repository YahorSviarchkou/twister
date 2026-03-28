package com.twister.workflow.service.repair;

import com.twister.domain.repair.StatusHistory;
import com.twister.workflow.dao.repair.StatusHistoryDao;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class StatusHistoryService<W, H extends StatusHistory<S>, S> {

    public abstract Class<W> getWorkflowClass();

    protected abstract StatusHistoryDao<H> getStatusHistoryDao();

    protected abstract H buildHistory(Long workflowItemId, S status);

    public Optional<H> getLastWorkflowStatusHistory(Long workflowItemId) {
        return getStatusHistoryDao().findFirstByWorkflowItemIdOrderByCreatedAtDesc(workflowItemId);
    }

    public S getLastWorkflowStatus(Long workflowItemId) {
        Optional<H> lastWorkflowStatusHistory = getLastWorkflowStatusHistory(workflowItemId);
        return lastWorkflowStatusHistory
                .map(StatusHistory::getStatus)
                .orElseThrow(() ->
                        new NoSuchElementException("Status history not found for workflow %s by workflow item id: %s"
                                .formatted(getWorkflowClassName(), workflowItemId)));
    }

    public List<H> getWorkflowStatusHistory(Long workflowItemId) {
        List<H> statusHistory = getStatusHistoryDao().findByWorkflowItemId(workflowItemId);
        return statusHistory.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(statusHistory);
    }

    public S updateWorkflowStatus(Long workflowItemId, S status) {
        String workflowClassName = getWorkflowClassName();
        log.info("Updating {} workflow item id: {}, with status: {}", workflowClassName, workflowItemId, status);

        Optional<H> lastHistory = getLastWorkflowStatusHistory(workflowItemId);

        if (lastHistory.isPresent() && lastHistory.get().getStatus() == status) {
            log.warn(
                    "Status {} already defined for {} workflow item with id: {}",
                    status,
                    workflowClassName,
                    workflowItemId);
            return status;
        }

        H history = buildHistory(workflowItemId, status);
        validateHistory(history, workflowItemId, status);

        H dbHistory = getStatusHistoryDao().save(history);
        log.info(
                "Status: {}, successfully applied to {} workflow item id: {}, historyId: {}",
                status,
                workflowClassName,
                workflowItemId,
                dbHistory.getId());
        return status;
    }

    protected void validateHistory(H history, Long workflowItemId, S status) {
        if (history.getWorkflowItemId() == null || history.getStatus() == null) {
            throw new IllegalStateException("History fields are not defined for workflowItemId: %s, status: %s"
                    .formatted(workflowItemId, status));
        }
    }

    private String getWorkflowClassName() {
        return getWorkflowClass().getSimpleName();
    }
}

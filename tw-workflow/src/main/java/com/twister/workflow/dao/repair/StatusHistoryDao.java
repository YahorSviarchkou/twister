package com.twister.workflow.dao.repair;

import java.util.List;
import java.util.Optional;

public interface StatusHistoryDao<T> {

    Optional<T> findFirstByWorkflowItemIdOrderByCreatedAtDesc(Long workflowItemId);

    List<T> findByWorkflowItemId(Long workflowItemId);

    T save(T history);
}

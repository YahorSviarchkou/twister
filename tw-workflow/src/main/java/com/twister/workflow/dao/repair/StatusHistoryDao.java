package com.twister.workflow.dao.repair;

import java.util.List;
import java.util.Optional;

public interface StatusHistoryDao<H> {

    Optional<H> findFirstByWorkflowItemIdOrderByCreatedAtDesc(Long workflowItemId);

    List<H> findByWorkflowItemId(Long workflowItemId);

    H save(H history);
}

package com.twister.persistence.repository.repair;

import com.twister.persistence.entity.repair.RepairTaskItemStatusHistoryEntity;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RepairTaskItemStatusHistoryRepository
        extends StatusHistoryRepository<RepairTaskItemStatusHistoryEntity> {

    @SuppressWarnings("JpaQlInspection")
    @Query("SELECT h FROM RepairTaskItemStatusHistoryEntity h WHERE h.workflowItem.task.id = :taskId")
    List<RepairTaskItemStatusHistoryEntity> findAllTaskItemsStatusesByTaskId(@Param("taskId") Long taskId);
}

package com.twister.repository.repair;

import com.twister.model.repair.RepairTaskStatusHistory;
import org.springframework.stereotype.Repository;

@Repository
public interface RepairTaskStatusHistoryRepository
    extends StatusHistoryRepository<RepairTaskStatusHistory> {
}

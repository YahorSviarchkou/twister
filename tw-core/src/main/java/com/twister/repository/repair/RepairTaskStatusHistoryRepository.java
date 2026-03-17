package com.twister.repository.repair;

import com.twister.domain.repair.RepairTaskStatusHistory;
import org.springframework.stereotype.Repository;

@Repository
public interface RepairTaskStatusHistoryRepository
    extends StatusHistoryRepository<RepairTaskStatusHistory> {
}

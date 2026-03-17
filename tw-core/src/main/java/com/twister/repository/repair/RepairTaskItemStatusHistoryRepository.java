package com.twister.repository.repair;

import com.twister.domain.repair.RepairTaskItemStatusHistory;
import org.springframework.stereotype.Repository;

@Repository
public interface RepairTaskItemStatusHistoryRepository
    extends StatusHistoryRepository<RepairTaskItemStatusHistory> {
}

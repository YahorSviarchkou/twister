package com.twister.persistence.repository.repair;

import com.twister.domain.repair.RepairTaskStatusHistory;
import com.twister.repository.repair.StatusHistoryRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepairTaskStatusHistoryRepository
    extends StatusHistoryRepository<RepairTaskStatusHistory> {
}

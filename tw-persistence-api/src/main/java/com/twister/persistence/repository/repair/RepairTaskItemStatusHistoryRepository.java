package com.twister.persistence.repository.repair;

import com.twister.domain.repair.RepairTaskItemStatusHistory;
import com.twister.repository.repair.StatusHistoryRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepairTaskItemStatusHistoryRepository
    extends StatusHistoryRepository<RepairTaskItemStatusHistory> {
}

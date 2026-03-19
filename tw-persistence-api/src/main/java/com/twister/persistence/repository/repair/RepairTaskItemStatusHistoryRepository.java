package com.twister.persistence.repository.repair;

import com.twister.persistence.entity.repair.RepairTaskItemStatusHistoryEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface RepairTaskItemStatusHistoryRepository
    extends StatusHistoryRepository<RepairTaskItemStatusHistoryEntity> {
}

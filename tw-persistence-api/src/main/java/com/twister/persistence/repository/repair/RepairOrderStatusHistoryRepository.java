package com.twister.persistence.repository.repair;

import com.twister.persistence.entity.repair.RepairOrderStatusHistoryEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface RepairOrderStatusHistoryRepository
    extends StatusHistoryRepository<RepairOrderStatusHistoryEntity> {
}

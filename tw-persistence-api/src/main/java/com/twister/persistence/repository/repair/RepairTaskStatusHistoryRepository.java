package com.twister.persistence.repository.repair;

import com.twister.persistence.entity.repair.RepairTaskStatusHistoryEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface RepairTaskStatusHistoryRepository extends StatusHistoryRepository<RepairTaskStatusHistoryEntity> {}

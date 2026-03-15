package com.twister.repository.repair;

import com.twister.model.repair.RepairOrderStatusHistory;
import org.springframework.stereotype.Repository;

@Repository
public interface RepairOrderStatusHistoryRepository
    extends StatusHistoryRepository<RepairOrderStatusHistory> {
}

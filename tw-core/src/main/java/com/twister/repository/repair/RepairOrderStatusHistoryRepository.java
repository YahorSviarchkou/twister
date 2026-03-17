package com.twister.repository.repair;

import com.twister.domain.repair.RepairOrderStatusHistory;
import org.springframework.stereotype.Repository;

@Repository
public interface RepairOrderStatusHistoryRepository
    extends StatusHistoryRepository<RepairOrderStatusHistory> {
}

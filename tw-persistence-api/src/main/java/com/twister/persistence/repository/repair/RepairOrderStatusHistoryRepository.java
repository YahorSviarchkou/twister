package com.twister.persistence.repository.repair;

import com.twister.domain.repair.RepairOrderStatusHistory;
import com.twister.repository.repair.StatusHistoryRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepairOrderStatusHistoryRepository
    extends StatusHistoryRepository<RepairOrderStatusHistory> {
}

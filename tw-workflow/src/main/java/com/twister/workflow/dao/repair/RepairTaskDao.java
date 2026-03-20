package com.twister.workflow.dao.repair;

import com.twister.domain.repair.RepairTask;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RepairTaskDao {

    Optional<RepairTask> findById(Long id);

    Optional<RepairTask> findByOrderId(Long orderId);

    Page<RepairTask> findAll(Pageable pageable);

    RepairTask save(RepairTask repairTask);
}

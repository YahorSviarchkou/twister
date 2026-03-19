package com.twister.workflow.dao.repair;

import com.twister.domain.repair.RepairTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface RepairTaskDao {

    Optional<RepairTask> findById(Long id);

    Optional<RepairTask> findByOrderId(Long orderId);

    Page<RepairTask> findAll(Pageable pageable);

    RepairTask save(RepairTask repairTask);
}

package com.twister.workflow.dao.repair;

import com.twister.domain.repair.RepairOrder;
import com.twister.payload.RepairOrderFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface RepairOrderDao {

    Optional<RepairOrder> findById(Long id);

    Optional<RepairOrder> findRepairOrderByTaskId(Long taskId);

    Page<RepairOrder> findAll(RepairOrderFilter filter, Pageable pageable);

    Page<RepairOrder> findAll(Pageable pageable);

    RepairOrder save(RepairOrder repairOrder);
}

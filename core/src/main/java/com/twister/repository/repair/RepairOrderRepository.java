package com.twister.repository.repair;

import com.twister.model.repair.RepairOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepairOrderRepository extends
    JpaRepository<RepairOrder, Long>, JpaSpecificationExecutor<RepairOrder> {

    Optional<RepairOrder> findRepairOrderByTask_Id(Long taskId);
}

package com.twister.persistence.repository.repair;

import com.twister.persistence.entity.repair.RepairOrderEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RepairOrderRepository
        extends JpaRepository<RepairOrderEntity, Long>, JpaSpecificationExecutor<RepairOrderEntity> {

    Optional<RepairOrderEntity> findRepairOrderByTask_Id(Long taskId);
}

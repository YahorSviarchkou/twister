package com.twister.persistence.repository.repair;

import com.twister.persistence.entity.repair.RepairTaskItemEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepairTaskItemRepository extends JpaRepository<RepairTaskItemEntity, Long> {

    List<RepairTaskItemEntity> findRepairTaskItemByTask_Id(Long taskId);
}

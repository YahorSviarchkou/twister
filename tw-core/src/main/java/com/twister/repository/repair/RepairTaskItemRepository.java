package com.twister.repository.repair;

import com.twister.domain.repair.RepairTaskItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepairTaskItemRepository extends JpaRepository<RepairTaskItem, Long> {

    List<RepairTaskItem> findRepairTaskItemByTask_Id(Long taskId);
}

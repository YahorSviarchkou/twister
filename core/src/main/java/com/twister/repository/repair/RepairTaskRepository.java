package com.twister.repository.repair;

import com.twister.model.repair.RepairTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepairTaskRepository extends JpaRepository<RepairTask, Long> {

    Optional<RepairTask> findByOrder_Id(Long orderId);
}

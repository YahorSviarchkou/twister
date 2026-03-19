package com.twister.persistence.repository.repair;

import com.twister.persistence.entity.repair.RepairTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepairTaskRepository extends JpaRepository<RepairTaskEntity, Long> {

    Optional<RepairTaskEntity> findByOrder_Id(Long orderId);
}

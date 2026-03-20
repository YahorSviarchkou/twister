package com.twister.persistence.repository.repair;

import com.twister.persistence.entity.repair.RepairInvoiceEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepairInvoiceRepository extends JpaRepository<RepairInvoiceEntity, Long> {

    Optional<RepairInvoiceEntity> findRepairInvoiceByOrder_Id(Long orderId);
}

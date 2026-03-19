package com.twister.persistence.repository.repair;

import com.twister.persistence.entity.repair.RepairInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepairInvoiceRepository extends JpaRepository<RepairInvoice, Long> {

    Optional<RepairInvoice> findRepairInvoiceByOrder_Id(Long orderId);
}

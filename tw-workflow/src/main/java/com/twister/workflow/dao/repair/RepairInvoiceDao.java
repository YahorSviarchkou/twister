package com.twister.workflow.dao.repair;

import com.twister.domain.repair.RepairInvoice;

import java.util.Optional;

public interface RepairInvoiceDao {

    Optional<RepairInvoice> findById(Long id);

    Optional<RepairInvoice> findRepairInvoiceByOrderId(Long orderId);

    RepairInvoice save(RepairInvoice invoice);
}

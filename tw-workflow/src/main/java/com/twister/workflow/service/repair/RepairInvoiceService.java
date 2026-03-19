package com.twister.workflow.service.repair;

import com.twister.domain.reference.ServiceType;
import com.twister.domain.reference.Spare;
import com.twister.domain.repair.RepairInvoice;
import com.twister.domain.repair.RepairOrder;
import com.twister.domain.repair.RepairTask;
import com.twister.domain.repair.RepairTaskItem;
import com.twister.workflow.dao.repair.RepairInvoiceDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RepairInvoiceService {

    private static final BigDecimal ZERO = BigDecimal.ZERO;

    private final RepairInvoiceDao repairInvoiceDao;
    private final RepairOrderService repairOrderService;
    private final RepairTaskService repairTaskService;
    private final RepairTaskItemService repairTaskItemService;

    public RepairInvoice getRepairInvoiceById(Long id) {
        return repairInvoiceDao.findById(id)
            .orElseThrow(() -> new NoSuchElementException("RepairInvoice not found by id: " + id));
    }

    public RepairInvoice getRepairInvoiceByOrderId(Long orderId) {
        return repairInvoiceDao.findRepairInvoiceByOrderId(orderId)
            .orElseThrow(() -> new NoSuchElementException("RepairInvoice not found by order id: " + orderId));
    }

    public RepairInvoice generateRepairInvoice(BigDecimal diagnosticPrice,
                                               String description,
                                               Long orderId) {
        RepairOrder repairOrder = repairOrderService.getRepairOrderById(orderId);

        RepairTask repairTask = repairTaskService.getRepairTaskByOrderId(orderId);
        List<RepairTaskItem> repairTaskItems = repairTaskItemService.getAllByTaskId(repairTask.getId());

        RepairInvoice invoice = new RepairInvoice();
        invoice.setOrder(repairOrder);
        invoice.setDiagnosticPrice(Optional.ofNullable(diagnosticPrice).orElse(ZERO));
        invoice.setLaborPrice(calculateLaborPrice(repairTaskItems));
        invoice.setSparePrice(calculateSparesPrice(repairTaskItems));
        invoice.setDescription(description);

        RepairInvoice dbRepairInvoice = repairInvoiceDao.save(invoice);
        log.info("Repair invoice with id: {}, successfully generated for order id: {}",
            dbRepairInvoice.getId(), orderId
        );
        return dbRepairInvoice;
    }

    private BigDecimal calculateLaborPrice(List<RepairTaskItem> repairTaskItems) {
        return repairTaskItems.stream()
            .map(RepairTaskItem::getServiceType)
            .map(ServiceType::getPrice)
            .reduce(BigDecimal::add)
            .orElse(ZERO);
    }

    private BigDecimal calculateSparesPrice(List<RepairTaskItem> repairTaskItems) {
        return repairTaskItems.stream()
            .map(RepairTaskItem::getSpares)
            .flatMap(Collection::stream)
            .map(Spare::getPrice)
            .reduce(BigDecimal::add)
            .orElse(ZERO);
    }
}

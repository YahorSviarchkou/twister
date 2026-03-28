package com.twister.workflow.service.repair;

import com.twister.domain.Customer;
import com.twister.domain.reference.Transport;
import com.twister.domain.repair.RepairOrder;
import com.twister.domain.repair.RepairOrderStatus;
import com.twister.payload.RepairOrderFilter;
import com.twister.workflow.dao.repair.RepairOrderDao;
import com.twister.workflow.service.CustomerService;
import com.twister.workflow.service.reference.TransportService;
import com.twister.workflow.service.workflow.WorkflowService;
import com.twister.workflow.service.workflow.event.RepairOrderEvent;
import com.twister.workflow.service.workflow.event.WorkflowEvent;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RepairOrderService implements WorkflowService<RepairOrder> {

    private final RepairOrderDao repairOrderDao;
    private final CustomerService customerService;
    private final TransportService transportService;
    private final RepairOrderStatusHistoryService historyService;

    @Override
    public Class<? extends WorkflowEvent<RepairOrder>> getWorkflowEvent() {
        return RepairOrderEvent.class;
    }

    @Override
    public boolean existsById(Long id) {
        return repairOrderDao.existsById(id);
    }

    public RepairOrder getRepairOrderById(Long id) {
        return repairOrderDao
                .findById(id)
                .orElseThrow(() -> new NoSuchElementException("RepairOrder not found by id: " + id));
    }

    public RepairOrder getRepairOrderByTaskId(Long taskId) {
        return repairOrderDao
                .findRepairOrderByTaskId(taskId)
                .orElseThrow(() -> new NoSuchElementException("RepairOrder not found by repair task id: " + taskId));
    }

    public Page<RepairOrder> findAll(RepairOrderFilter filter, Pageable pageable) {
        return repairOrderDao.findAll(filter, pageable);
    }

    public Page<RepairOrder> findAll(Pageable pageable) {
        return repairOrderDao.findAll(pageable);
    }

    @Transactional
    public void create(RepairOrder repairOrder) {
        if (repairOrder == null) {
            throw new IllegalArgumentException("RepairOrder not exist for creating");
        }
        if (repairOrder.getCustomer() == null || repairOrder.getTransport() == null) {
            throw new IllegalStateException("RepairOrder must contains customer and transport on creating");
        }

        log.info("Creating repair order");
        Customer customer =
                customerService.getCustomerById(repairOrder.getCustomer().getId());
        Transport transport =
                transportService.getTransportById(repairOrder.getTransport().getId());

        repairOrder.setId(null);
        repairOrder.setCustomer(customer);
        repairOrder.setTransport(transport);

        RepairOrder dbRepairOrder = repairOrderDao.save(repairOrder);
        log.info("RepairOrder successfully created with id: {}", dbRepairOrder.getId());

        historyService.updateWorkflowStatus(dbRepairOrder.getId(), RepairOrderStatus.OPEN);
    }
}

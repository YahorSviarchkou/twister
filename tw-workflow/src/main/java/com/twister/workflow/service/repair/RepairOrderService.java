package com.twister.workflow.service.repair;

import com.twister.domain.Customer;
import com.twister.domain.reference.Transport;
import com.twister.domain.repair.RepairOrder;
import com.twister.domain.repair.RepairOrderStatus;
import com.twister.repository.repair.RepairOrderRepository;
import com.twister.payload.RepairOrderFilter;
import com.twister.repository.specification.RepairOrderSpecification;
import com.twister.service.CustomerService;
import com.twister.workflow.service.reference.TransportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class RepairOrderService {

    private final RepairOrderRepository repository;
    private final CustomerService customerService;
    private final TransportService transportService;
    private final RepairOrderStatusHistoryService historyService;

    public RepairOrder getRepairOrderById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("RepairOrder not found by id: " + id));
    }

    public RepairOrder getRepairOrderByTaskId(Long taskId) {
        return repository.findRepairOrderByTask_Id(taskId)
            .orElseThrow(() -> new NoSuchElementException("RepairOrder not found by repair task id: " + taskId));
    }

    public Page<RepairOrder> findAll(RepairOrderFilter filter, Pageable pageable) {
        Specification<RepairOrder> specification = RepairOrderSpecification.search(filter);
        return repository.findAll(specification, pageable);
    }

    public Page<RepairOrder> findAll(Pageable pageable) {
        return repository.findAll(pageable);
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
        Customer customer = customerService.getCustomerById(repairOrder.getCustomer().getId());
        Transport transport = transportService.getTransportById(repairOrder.getTransport().getId());

        repairOrder.setId(null);
        repairOrder.setCustomer(customer);
        repairOrder.setTransport(transport);

        RepairOrder dbRepairOrder = repository.save(repairOrder);
        log.info("RepairOrder successfully created with id: {}", dbRepairOrder.getId());

        historyService.updateWorkflowStatus(dbRepairOrder.getId(), RepairOrderStatus.OPEN);
    }
}

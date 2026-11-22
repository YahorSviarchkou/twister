package com.twister.persistence.gen.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.twister.persistence.BaseIntegrationTest;
import com.twister.persistence.entity.CustomerEntity;
import com.twister.persistence.entity.reference.ServiceTypeEntity;
import com.twister.persistence.entity.reference.TransportEntity;
import com.twister.persistence.entity.repair.RepairInvoiceEntity;
import com.twister.persistence.entity.repair.RepairOrderEntity;
import com.twister.persistence.entity.repair.RepairTaskEntity;
import com.twister.persistence.entity.repair.RepairTaskItemEntity;
import com.twister.persistence.repository.CustomerRepository;
import com.twister.persistence.repository.reference.ServiceTypeRepository;
import com.twister.persistence.repository.repair.RepairInvoiceRepository;
import com.twister.persistence.repository.repair.RepairOrderRepository;
import com.twister.persistence.repository.repair.RepairTaskItemRepository;
import com.twister.persistence.repository.repair.RepairTaskRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CustomRepositoryMethodsTest extends BaseIntegrationTest {

    @Autowired
    private RepairInvoiceRepository invoiceRepository;

    @Autowired
    private RepairTaskRepository taskRepository;

    @Autowired
    private RepairTaskItemRepository itemRepository;

    @Autowired
    private RepairOrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ServiceTypeRepository serviceTypeRepository;

    @Autowired
    private com.twister.persistence.repository.reference.TransportRepository transportRepository;

    @Test
    void shouldFindRelationsCorrectly_UsingCustomMethods() {
        // Arrange
        CustomerEntity customer = customerRepository.save(CustomerEntity.builder()
                .name("Alex")
                .surname("One")
                .patronymic("Testovich")
                .phone("123")
                .build());

        TransportEntity transport =
                transportRepository.save(TransportEntity.builder().name("Car").build());

        RepairOrderEntity order = orderRepository.save(RepairOrderEntity.builder()
                .description("Desc")
                .customer(customer)
                .transport(transport)
                .build());

        RepairTaskEntity task =
                taskRepository.save(RepairTaskEntity.builder().order(order).build());

        ServiceTypeEntity serviceType = serviceTypeRepository.save(
                ServiceTypeEntity.builder().name("Repair").build());

        RepairTaskItemEntity item = itemRepository.save(RepairTaskItemEntity.builder()
                .task(task)
                .serviceType(serviceType)
                .build());

        RepairInvoiceEntity invoice = invoiceRepository.save(RepairInvoiceEntity.builder()
                .order(order)
                .diagnosticPrice(new BigDecimal("100.50"))
                .build());

        // Act & Assert
        Optional<RepairInvoiceEntity> foundInvoice = invoiceRepository.findRepairInvoiceByOrder_Id(order.getId());
        assertThat(foundInvoice).isPresent();
        assertThat(foundInvoice.get().getDiagnosticPrice()).isEqualByComparingTo("100.50");

        Optional<RepairTaskEntity> foundTask = taskRepository.findByOrder_Id(order.getId());
        assertThat(foundTask).isPresent();
        assertThat(foundTask.get().getId()).isEqualTo(task.getId());

        List<RepairTaskItemEntity> foundItems = itemRepository.findRepairTaskItemByTask_Id(task.getId());
        assertThat(foundItems).hasSize(1);
        assertThat(foundItems.get(0).getId()).isEqualTo(item.getId());
    }
}

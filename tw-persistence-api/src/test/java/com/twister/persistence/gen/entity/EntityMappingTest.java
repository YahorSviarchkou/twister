package com.twister.persistence.gen.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.twister.persistence.BaseIntegrationTest;
import com.twister.persistence.entity.CustomerEntity;
import com.twister.persistence.entity.reference.ServiceTypeEntity;
import com.twister.persistence.entity.reference.TransportEntity;
import com.twister.persistence.entity.repair.RepairOrderEntity;
import com.twister.persistence.entity.repair.RepairTaskEntity;
import com.twister.persistence.entity.repair.RepairTaskItemEntity;
import com.twister.persistence.repository.CustomerRepository;
import com.twister.persistence.repository.reference.ServiceTypeRepository;
import com.twister.persistence.repository.reference.TransportRepository;
import com.twister.persistence.repository.repair.RepairOrderRepository;
import com.twister.persistence.repository.repair.RepairTaskItemRepository;
import com.twister.persistence.repository.repair.RepairTaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

public class EntityMappingTest extends BaseIntegrationTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RepairOrderRepository repairOrderRepository;

    @Autowired
    private RepairTaskRepository repairTaskRepository;

    @Autowired
    private RepairTaskItemRepository repairTaskItemRepository;

    @Autowired
    private ServiceTypeRepository serviceTypeRepository;

    @Autowired
    private TransportRepository transportRepository;

    @Test
    void shouldMapAuditableFieldsCorrectly_WhenEntitySaved() {
        CustomerEntity customer = CustomerEntity.builder()
                .name("Test")
                .surname("Customer")
                .patronymic("Jr.")
                .phone("+1234567890")
                .email("test.customer@example.com")
                .build();

        CustomerEntity savedCustomer = customerRepository.saveAndFlush(customer);

        assertThat(savedCustomer.getId()).isNotNull();
    }

    @Test
    void shouldThrowException_WhenRequiredFieldIsMissing() {
        CustomerEntity customerWithoutSurname =
                CustomerEntity.builder().name("Test").patronymic("Jr.").build();

        assertThrows(
                DataIntegrityViolationException.class, () -> customerRepository.saveAndFlush(customerWithoutSurname));
    }

    @Test
    void shouldSaveAndFetch_OneToManyRelations() {
        CustomerEntity customer = customerRepository.save(CustomerEntity.builder()
                .name("Alex")
                .surname("Smith")
                .patronymic("I")
                .phone("123")
                .build());

        TransportEntity transport =
                transportRepository.save(TransportEntity.builder().name("Bike").build());

        RepairOrderEntity order = repairOrderRepository.save(RepairOrderEntity.builder()
                .description("Fix brakes")
                .customer(customer)
                .transport(transport)
                .build());

        RepairTaskEntity task = repairTaskRepository.save(
                RepairTaskEntity.builder().order(order).build());

        ServiceTypeEntity st = serviceTypeRepository.save(
                ServiceTypeEntity.builder().name("Adjustment").build());

        RepairTaskItemEntity item = repairTaskItemRepository.save(
                RepairTaskItemEntity.builder().task(task).serviceType(st).build());

        RepairTaskEntity foundTask = repairTaskRepository.findById(task.getId()).orElseThrow();

        assertThat(foundTask).isNotNull();
    }
}

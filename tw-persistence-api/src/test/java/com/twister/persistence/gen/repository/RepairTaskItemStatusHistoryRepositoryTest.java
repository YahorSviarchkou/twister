package com.twister.persistence.gen.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.twister.domain.repair.RepairTaskItemStatus;
import com.twister.persistence.BaseIntegrationTest;
import com.twister.persistence.entity.CustomerEntity;
import com.twister.persistence.entity.reference.ServiceTypeEntity;
import com.twister.persistence.entity.reference.TransportEntity;
import com.twister.persistence.entity.repair.RepairOrderEntity;
import com.twister.persistence.entity.repair.RepairTaskEntity;
import com.twister.persistence.entity.repair.RepairTaskItemEntity;
import com.twister.persistence.entity.repair.RepairTaskItemStatusHistoryEntity;
import com.twister.persistence.repository.CustomerRepository;
import com.twister.persistence.repository.reference.ServiceTypeRepository;
import com.twister.persistence.repository.reference.TransportRepository;
import com.twister.persistence.repository.repair.RepairOrderRepository;
import com.twister.persistence.repository.repair.RepairTaskItemRepository;
import com.twister.persistence.repository.repair.RepairTaskItemStatusHistoryRepository;
import com.twister.persistence.repository.repair.RepairTaskRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class RepairTaskItemStatusHistoryRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private RepairTaskItemStatusHistoryRepository historyRepository;

    @Autowired
    private RepairTaskItemRepository itemRepository;

    @Autowired
    private RepairTaskRepository taskRepository;

    @Autowired
    private RepairOrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TransportRepository transportRepository;

    @Autowired
    private ServiceTypeRepository serviceTypeRepository;

    @Test
    void shouldFindAllTaskItemsStatusesByTaskId() {
        CustomerEntity customer = customerRepository.save(CustomerEntity.builder()
                .name("Test")
                .surname("Customer")
                .patronymic("Testovich")
                .phone("555-0000")
                .build());

        TransportEntity transport =
                transportRepository.save(TransportEntity.builder().name("Bus").build());

        RepairOrderEntity order = orderRepository.save(RepairOrderEntity.builder()
                .description("Test Order")
                .customer(customer)
                .transport(transport)
                .build());

        RepairTaskEntity task =
                taskRepository.save(RepairTaskEntity.builder().order(order).build());

        ServiceTypeEntity serviceType = serviceTypeRepository.save(
                ServiceTypeEntity.builder().name("Oil Change").build());

        RepairTaskItemEntity taskItem = itemRepository.save(RepairTaskItemEntity.builder()
                .task(task)
                .serviceType(serviceType)
                .build());

        RepairTaskItemStatusHistoryEntity history1 = historyRepository.save(RepairTaskItemStatusHistoryEntity.builder()
                .workflowItem(taskItem)
                .status(RepairTaskItemStatus.CREATED)
                .build());

        RepairTaskItemStatusHistoryEntity history2 = historyRepository.save(RepairTaskItemStatusHistoryEntity.builder()
                .workflowItem(taskItem)
                .status(RepairTaskItemStatus.IN_PROGRESS)
                .build());

        List<RepairTaskItemStatusHistoryEntity> results =
                historyRepository.findAllTaskItemsStatusesByTaskId(task.getId());

        assertThat(results)
                .hasSize(2)
                .extracting("status")
                .containsExactlyInAnyOrder(RepairTaskItemStatus.CREATED, RepairTaskItemStatus.IN_PROGRESS);
    }
}

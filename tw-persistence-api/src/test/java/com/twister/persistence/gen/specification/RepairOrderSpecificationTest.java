package com.twister.persistence.gen.specification;

import static org.assertj.core.api.Assertions.assertThat;

import com.twister.payload.RepairOrderFilter;
import com.twister.persistence.BaseIntegrationTest;
import com.twister.persistence.entity.CustomerEntity;
import com.twister.persistence.entity.reference.TransportEntity;
import com.twister.persistence.entity.repair.RepairOrderEntity;
import com.twister.persistence.repository.CustomerRepository;
import com.twister.persistence.repository.reference.TransportRepository;
import com.twister.persistence.repository.repair.RepairOrderRepository;
import com.twister.persistence.repository.specification.RepairOrderSpecification;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;

public class RepairOrderSpecificationTest extends BaseIntegrationTest {

    @Autowired
    private RepairOrderRepository repairOrderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TransportRepository transportRepository;

    private CustomerEntity customer;
    private TransportEntity transport;
    private RepairOrderEntity order;

    @BeforeEach
    void setUp() {
        repairOrderRepository.deleteAll();
        customerRepository.deleteAll();
        transportRepository.deleteAll();

        customer = customerRepository.save(CustomerEntity.builder()
                .name("John")
                .surname("Doe")
                .patronymic("Testovich")
                .phone("12345")
                .email("john@example.com")
                .build());

        transport =
                transportRepository.save(TransportEntity.builder().name("Car").build());

        order = repairOrderRepository.save(RepairOrderEntity.builder()
                .description("Fix engine")
                .customer(customer)
                .transport(transport)
                .build());
    }

    @Test
    void shouldFindOrder_WhenDescriptionMatches() {
        RepairOrderFilter filter = new RepairOrderFilter("engine", null, null);
        Specification<RepairOrderEntity> spec = RepairOrderSpecification.search(filter);

        List<RepairOrderEntity> results = repairOrderRepository.findAll(spec);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getDescription()).isEqualTo("Fix engine");
    }

    @Test
    void shouldFindOrder_WhenCustomerIdMatches() {
        RepairOrderFilter filter = new RepairOrderFilter(null, customer.getId(), null);
        Specification<RepairOrderEntity> spec = RepairOrderSpecification.search(filter);

        List<RepairOrderEntity> results = repairOrderRepository.findAll(spec);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getCustomer().getId()).isEqualTo(customer.getId());
    }

    @Test
    void shouldFindOrder_WhenTransportIdMatches() {
        RepairOrderFilter filter = new RepairOrderFilter(null, null, transport.getId());
        Specification<RepairOrderEntity> spec = RepairOrderSpecification.search(filter);

        List<RepairOrderEntity> results = repairOrderRepository.findAll(spec);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTransport().getId()).isEqualTo(transport.getId());
    }
}

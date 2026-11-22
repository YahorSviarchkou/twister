package com.twister.persistence.gen.specification;

import static org.assertj.core.api.Assertions.assertThat;

import com.twister.payload.CustomerFilter;
import com.twister.persistence.BaseIntegrationTest;
import com.twister.persistence.entity.CustomerEntity;
import com.twister.persistence.repository.CustomerRepository;
import com.twister.persistence.repository.specification.CustomerSpecification;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;

public class CustomerSpecificationTest extends BaseIntegrationTest {

    @Autowired
    private CustomerRepository customerRepository;

    private CustomerEntity customer;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();

        customer = customerRepository.save(CustomerEntity.builder()
                .name("Alex")
                .surname("Smirnov")
                .patronymic("Ivanovich")
                .phone("+1234567")
                .email("alex@example.com")
                .build());
    }

    @Test
    void shouldFindCustomer_WhenNameMatches() {
        CustomerFilter filter = CustomerFilter.builder().name("lex").build();
        Specification<CustomerEntity> spec = CustomerSpecification.search(filter);

        List<CustomerEntity> results = customerRepository.findAll(spec);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Alex");
    }

    @Test
    void shouldFindCustomer_WhenPhoneMatches() {
        CustomerFilter filter = CustomerFilter.builder().phone("3456").build();
        Specification<CustomerEntity> spec = CustomerSpecification.search(filter);

        List<CustomerEntity> results = customerRepository.findAll(spec);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getPhone()).isEqualTo("+1234567");
    }

    @Test
    void shouldFindCustomer_WhenAllFieldsMatch() {
        CustomerFilter filter = CustomerFilter.builder()
                .name("lex")
                .surname("mir")
                .patronymic("van")
                .phone("345")
                .build();
        Specification<CustomerEntity> spec = CustomerSpecification.search(filter);

        List<CustomerEntity> results = customerRepository.findAll(spec);

        assertThat(results).hasSize(1);
    }
}

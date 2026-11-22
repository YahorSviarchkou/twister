package com.twister.persistence.reference;

import static org.assertj.core.api.Assertions.assertThat;

import com.twister.persistence.BaseIntegrationTest;
import com.twister.persistence.entity.reference.ServiceTypeEntity;
import com.twister.persistence.repository.reference.ServiceTypeRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public class ReferenceRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ServiceTypeRepository serviceTypeRepository;

    private ServiceTypeEntity service1;
    private ServiceTypeEntity service2;

    @BeforeEach
    void setUp() {
        serviceTypeRepository.deleteAll();

        service1 = serviceTypeRepository.save(
                ServiceTypeEntity.builder().name("OIL CHANGE").build());

        service2 = serviceTypeRepository.save(
                ServiceTypeEntity.builder().name("TIRE REPLACEMENT").build());
    }

    @Test
    void shouldFindByNameContainingIgnoreCase() {
        Page<ServiceTypeEntity> page =
                serviceTypeRepository.findByNameContainingIgnoreCase("oil", PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getName()).isEqualTo("OIL CHANGE");
    }

    @Test
    void shouldFindByIdIn() {
        Set<Long> idsToFind = Set.of(service1.getId(), service2.getId());

        List<ServiceTypeEntity> foundServices = serviceTypeRepository.findByIdIn(idsToFind);

        assertThat(foundServices).hasSize(2);
        List<String> foundNames =
                foundServices.stream().map(ServiceTypeEntity::getName).collect(Collectors.toList());
        assertThat(foundNames).containsExactlyInAnyOrder("OIL CHANGE", "TIRE REPLACEMENT");
    }
}

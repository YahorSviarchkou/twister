package com.twister.persistence.gen.specification;

import static org.assertj.core.api.Assertions.assertThat;

import com.twister.payload.SpareFilter;
import com.twister.persistence.BaseIntegrationTest;
import com.twister.persistence.entity.reference.SpareBrandEntity;
import com.twister.persistence.entity.reference.SpareEntity;
import com.twister.persistence.repository.reference.SpareBrandRepository;
import com.twister.persistence.repository.reference.SpareRepository;
import com.twister.persistence.repository.specification.SpareSpecification;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;

public class SpareSpecificationTest extends BaseIntegrationTest {

    @Autowired
    private SpareRepository spareRepository;

    @Autowired
    private SpareBrandRepository brandRepository;

    private SpareBrandEntity brand;

    @BeforeEach
    void setUp() {
        spareRepository.deleteAll();
        brandRepository.deleteAll();

        brand = brandRepository.save(SpareBrandEntity.builder().name("Bosch").build());

        spareRepository.save(SpareEntity.builder()
                .name("Spark Plug")
                .model("X-100")
                .material("Metal")
                .issueYear(2023)
                .brand(brand)
                .build());
    }

    @Test
    void shouldFindSpare_WhenNameAndBrandMatch() {
        SpareFilter filter = SpareFilter.builder()
                .name("plug")
                .model("x-1")
                .material("metal")
                .issueYear(2023)
                .brandId(brand.getId())
                .spareTypeId(1L)
                .transportTypeId(1L)
                .countryId(1L)
                // Since our dummy spare doesn't have country, spareType, transportType set we test the optionality of
                // other fields if they were present
                .build();
        Specification<SpareEntity> spec = SpareSpecification.search(filter);

        List<SpareEntity> results = spareRepository.findAll(spec);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Spark Plug");
    }
}

package com.twister.persistence.gen.specification;

import static org.assertj.core.api.Assertions.assertThat;

import com.twister.payload.TransportFilter;
import com.twister.persistence.BaseIntegrationTest;
import com.twister.persistence.entity.reference.CountryEntity;
import com.twister.persistence.entity.reference.TransportEntity;
import com.twister.persistence.repository.reference.CountryRepository;
import com.twister.persistence.repository.reference.TransportRepository;
import com.twister.persistence.repository.specification.TransportSpecification;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;

public class TransportSpecificationTest extends BaseIntegrationTest {

    @Autowired
    private TransportRepository transportRepository;

    @Autowired
    private CountryRepository countryRepository;

    private CountryEntity country;

    @BeforeEach
    void setUp() {
        transportRepository.deleteAll();
        countryRepository.deleteAll();

        country = countryRepository.save(CountryEntity.builder().name("Germany").build());

        transportRepository.save(TransportEntity.builder()
                .name("Sedan")
                .model("M-Ser")
                .country(country)
                .issueYear(2022)
                .build());
    }

    @Test
    void shouldFindTransport_WhenCountryMatches() {
        TransportFilter filter = TransportFilter.builder()
                .name("edan")
                .model("M-S")
                .countryId(country.getId())
                .brandId(1L)
                .transportTypeId(1L)
                .issueYear(2022)
                .build();
        Specification<TransportEntity> spec = TransportSpecification.search(filter);

        List<TransportEntity> results = transportRepository.findAll(spec);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Sedan");
    }
}

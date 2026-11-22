package com.twister.persistence.gen.specification;

import static org.assertj.core.api.Assertions.assertThat;

import com.twister.payload.TransportFilter;
import com.twister.payload.TransportFilter.TransportFilterBuilder;
import com.twister.persistence.BaseIntegrationTest;
import com.twister.persistence.entity.reference.CountryEntity;
import com.twister.persistence.entity.reference.TransportBrandEntity;
import com.twister.persistence.entity.reference.TransportEntity;
import com.twister.persistence.entity.reference.TransportTypeEntity;
import com.twister.persistence.repository.reference.CountryRepository;
import com.twister.persistence.repository.reference.TransportBrandRepository;
import com.twister.persistence.repository.reference.TransportRepository;
import com.twister.persistence.repository.reference.TransportTypeRepository;
import com.twister.persistence.repository.specification.TransportSpecification;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.test.context.TestConstructor;

@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class TransportSpecificationTest extends BaseIntegrationTest {

    private final TransportRepository transportRepository;
    private final TransportBrandRepository brandRepository;
    private final TransportTypeRepository typeRepository;
    private final CountryRepository countryRepository;

    private Relations matching;
    private Relations other;
    private TransportEntity target;

    @BeforeEach
    void setUp() {
        other = saveRelations("Other");
        matching = saveRelations("Matching");
        target = transportRepository.save(matchingTransport());
    }

    @ParameterizedTest
    @EnumSource
    void shouldFindOnlyTarget_WhenFilteringBySingleField(FilterField field) {
        saveDecoy(field);
        TransportFilterBuilder filter = TransportFilter.builder();
        field.select.accept(filter, target);

        assertThat(search(filter.build())).containsExactly(target.getId());
    }

    @Test
    void shouldFindOnlyTarget_WhenFilteringByAllFields() {
        TransportFilterBuilder filter = TransportFilter.builder();
        for (FilterField field : FilterField.values()) {
            saveDecoy(field);
            field.select.accept(filter, target);
        }

        assertThat(search(filter.build())).containsExactly(target.getId());
    }

    @Test
    void shouldFindEverything_WhenFilterIsEmpty() {
        List<Long> expected = new ArrayList<>(List.of(target.getId()));
        for (FilterField field : FilterField.values()) {
            expected.add(saveDecoy(field).getId());
        }
        // An unconditional inner join would drop this one.
        TransportEntity withoutRelations =
                transportRepository.save(TransportEntity.builder().name("Bare").build());
        expected.add(withoutRelations.getId());

        assertThat(search(TransportFilter.builder().build())).containsExactlyInAnyOrderElementsOf(expected);
    }

    private List<Long> search(TransportFilter filter) {
        return transportRepository.findAll(TransportSpecification.search(filter)).stream()
                .map(TransportEntity::getId)
                .toList();
    }

    private TransportEntity matchingTransport() {
        return TransportEntity.builder()
                .name("City Cruiser")
                .model("Urban-X200")
                .issueYear(2022)
                .brand(matching.brand())
                .type(matching.type())
                .country(matching.country())
                .build();
    }

    private TransportEntity saveDecoy(FilterField field) {
        TransportEntity decoy = matchingTransport();
        field.differ.accept(decoy, other);
        return transportRepository.save(decoy);
    }

    private Relations saveRelations(String name) {
        return new Relations(
                brandRepository.save(TransportBrandEntity.builder().name(name).build()),
                typeRepository.save(TransportTypeEntity.builder().name(name).build()),
                countryRepository.save(CountryEntity.builder().name(name).build()));
    }

    private record Relations(TransportBrandEntity brand, TransportTypeEntity type, CountryEntity country) {}

    @RequiredArgsConstructor
    private enum FilterField {
        // Text filters are inner substrings in another case, to need both wildcards and both lowercase conversions.
        NAME((decoy, other) -> decoy.setName("Mountain Bike"), (filter, target) -> filter.name("tY cRu")),
        MODEL((decoy, other) -> decoy.setModel("Trail-300"), (filter, target) -> filter.model("aN-x2")),
        ISSUE_YEAR((decoy, other) -> decoy.setIssueYear(2021), (filter, target) -> filter.issueYear(2022)),
        BRAND(
                (decoy, other) -> decoy.setBrand(other.brand()),
                (filter, target) -> filter.brandId(target.getBrand().getId())),
        TRANSPORT_TYPE(
                (decoy, other) -> decoy.setType(other.type()),
                (filter, target) -> filter.transportTypeId(target.getType().getId())),
        COUNTRY(
                (decoy, other) -> decoy.setCountry(other.country()),
                (filter, target) -> filter.countryId(target.getCountry().getId()));

        private final BiConsumer<TransportEntity, Relations> differ;
        private final BiConsumer<TransportFilterBuilder, TransportEntity> select;
    }
}

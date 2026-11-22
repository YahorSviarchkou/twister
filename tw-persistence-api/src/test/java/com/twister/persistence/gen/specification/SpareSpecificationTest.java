package com.twister.persistence.gen.specification;

import static org.assertj.core.api.Assertions.assertThat;

import com.twister.payload.SpareFilter;
import com.twister.payload.SpareFilter.SpareFilterBuilder;
import com.twister.persistence.BaseIntegrationTest;
import com.twister.persistence.entity.reference.CountryEntity;
import com.twister.persistence.entity.reference.SpareBrandEntity;
import com.twister.persistence.entity.reference.SpareEntity;
import com.twister.persistence.entity.reference.SpareTypeEntity;
import com.twister.persistence.entity.reference.TransportTypeEntity;
import com.twister.persistence.repository.reference.CountryRepository;
import com.twister.persistence.repository.reference.SpareBrandRepository;
import com.twister.persistence.repository.reference.SpareRepository;
import com.twister.persistence.repository.reference.SpareTypeRepository;
import com.twister.persistence.repository.reference.TransportTypeRepository;
import com.twister.persistence.repository.specification.SpareSpecification;
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
public class SpareSpecificationTest extends BaseIntegrationTest {

    private final SpareRepository spareRepository;
    private final SpareBrandRepository brandRepository;
    private final SpareTypeRepository typeRepository;
    private final TransportTypeRepository transportTypeRepository;
    private final CountryRepository countryRepository;

    private Relations matching;
    private Relations other;
    private SpareEntity target;

    @BeforeEach
    void setUp() {
        other = saveRelations("Other");
        matching = saveRelations("Matching");
        target = spareRepository.save(matchingSpare());
    }

    @ParameterizedTest
    @EnumSource
    void shouldFindOnlyTarget_WhenFilteringBySingleField(FilterField field) {
        saveDecoy(field);
        SpareFilterBuilder filter = SpareFilter.builder();
        field.select.accept(filter, target);

        assertThat(search(filter.build())).containsExactly(target.getId());
    }

    @Test
    void shouldFindOnlyTarget_WhenFilteringByAllFields() {
        SpareFilterBuilder filter = SpareFilter.builder();
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
        SpareEntity withoutRelations =
                spareRepository.save(SpareEntity.builder().name("Bare").build());
        expected.add(withoutRelations.getId());

        assertThat(search(SpareFilter.builder().build())).containsExactlyInAnyOrderElementsOf(expected);
    }

    private List<Long> search(SpareFilter filter) {
        return spareRepository.findAll(SpareSpecification.search(filter)).stream()
                .map(SpareEntity::getId)
                .toList();
    }

    private SpareEntity matchingSpare() {
        return SpareEntity.builder()
                .name("Spark Plug")
                .model("Turbo-X100")
                .material("Carbon Steel")
                .issueYear(2023)
                .brand(matching.brand())
                .type(matching.type())
                .transportType(matching.transportType())
                .country(matching.country())
                .build();
    }

    private SpareEntity saveDecoy(FilterField field) {
        SpareEntity decoy = matchingSpare();
        field.differ.accept(decoy, other);
        return spareRepository.save(decoy);
    }

    private Relations saveRelations(String name) {
        return new Relations(
                brandRepository.save(SpareBrandEntity.builder().name(name).build()),
                typeRepository.save(SpareTypeEntity.builder().name(name).build()),
                transportTypeRepository.save(
                        TransportTypeEntity.builder().name(name).build()),
                countryRepository.save(CountryEntity.builder().name(name).build()));
    }

    private record Relations(
            SpareBrandEntity brand, SpareTypeEntity type, TransportTypeEntity transportType, CountryEntity country) {}

    @RequiredArgsConstructor
    private enum FilterField {
        // Text filters are inner substrings in another case, to need both wildcards and both lowercase conversions.
        NAME((decoy, other) -> decoy.setName("Brake Pad"), (filter, target) -> filter.name("K pLu")),
        MODEL((decoy, other) -> decoy.setModel("Eco-200"), (filter, target) -> filter.model("bO-x1")),
        MATERIAL((decoy, other) -> decoy.setMaterial("Rubber"), (filter, target) -> filter.material("oN sTe")),
        ISSUE_YEAR((decoy, other) -> decoy.setIssueYear(2024), (filter, target) -> filter.issueYear(2023)),
        BRAND(
                (decoy, other) -> decoy.setBrand(other.brand()),
                (filter, target) -> filter.brandId(target.getBrand().getId())),
        SPARE_TYPE(
                (decoy, other) -> decoy.setType(other.type()),
                (filter, target) -> filter.spareTypeId(target.getType().getId())),
        TRANSPORT_TYPE(
                (decoy, other) -> decoy.setTransportType(other.transportType()),
                (filter, target) ->
                        filter.transportTypeId(target.getTransportType().getId())),
        COUNTRY(
                (decoy, other) -> decoy.setCountry(other.country()),
                (filter, target) -> filter.countryId(target.getCountry().getId()));

        private final BiConsumer<SpareEntity, Relations> differ;
        private final BiConsumer<SpareFilterBuilder, SpareEntity> select;
    }
}

package com.twister.persistence.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.twister.domain.AuditableModel;
import com.twister.domain.repair.RepairInvoice;
import com.twister.persistence.entity.AuditableEntity;
import com.twister.persistence.entity.reference.SpareEntity;
import com.twister.persistence.entity.repair.RepairInvoiceEntity;
import com.twister.persistence.entity.repair.RepairOrderEntity;
import com.twister.persistence.mapper.reference.CountryRepositoryMapper;
import com.twister.persistence.mapper.reference.ServiceTypeRepositoryMapper;
import com.twister.persistence.mapper.reference.SpareBrandRepositoryMapper;
import com.twister.persistence.mapper.reference.SpareRepositoryMapper;
import com.twister.persistence.mapper.reference.SpareTypeRepositoryMapper;
import com.twister.persistence.mapper.reference.TransportBrandRepositoryMapper;
import com.twister.persistence.mapper.reference.TransportRepositoryMapper;
import com.twister.persistence.mapper.reference.TransportTypeRepositoryMapper;
import com.twister.persistence.mapper.repair.RepairInvoiceRepositoryMapper;
import com.twister.persistence.mapper.repair.RepairOrderRepositoryMapper;
import com.twister.persistence.mapper.repair.RepairOrderStatusHistoryRepositoryMapper;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class RepositoryMapperTest {

    // Known bug, see the TODO on RepairOrderStatusHistoryRepositoryMapper: must not be asserted as correct.
    private static final String HISTORY_LINK = "(.*\\.)?workflowItem(Id)?";

    // Generated nested mappings wrongly copy these, so the mappings that do ignore them are asserted separately.
    private static final String CREATION_AUDIT = "(.*\\.)?created(At|By)";

    private static AnnotationConfigApplicationContext context;

    @BeforeAll
    static void startContext() {
        context = MapperTestContext.start();
    }

    @AfterAll
    static void closeContext() {
        context.close();
    }

    static Stream<MapperCase<?, ?>> mappers() {
        return Stream.of(
                new MapperCase<>(CountryRepositoryMapper.class, EntityFixtures::country, DomainFixtures::country),
                new MapperCase<>(
                        ServiceTypeRepositoryMapper.class, EntityFixtures::serviceType, DomainFixtures::serviceType),
                new MapperCase<>(
                        SpareBrandRepositoryMapper.class, EntityFixtures::spareBrand, DomainFixtures::spareBrand),
                new MapperCase<>(SpareTypeRepositoryMapper.class, EntityFixtures::spareType, DomainFixtures::spareType),
                new MapperCase<>(SpareRepositoryMapper.class, EntityFixtures::spare, DomainFixtures::spare),
                new MapperCase<>(
                        TransportBrandRepositoryMapper.class,
                        EntityFixtures::transportBrand,
                        DomainFixtures::transportBrand),
                new MapperCase<>(
                        TransportTypeRepositoryMapper.class,
                        EntityFixtures::transportType,
                        DomainFixtures::transportType),
                new MapperCase<>(TransportRepositoryMapper.class, EntityFixtures::transport, DomainFixtures::transport),
                new MapperCase<>(
                        RepairOrderStatusHistoryRepositoryMapper.class,
                        EntityFixtures::orderHistory,
                        DomainFixtures::orderHistory),
                new MapperCase<>(RepairOrderRepositoryMapper.class, EntityFixtures::order, DomainFixtures::order),
                new MapperCase<>(
                        RepairInvoiceRepositoryMapper.class, EntityFixtures::invoice, DomainFixtures::invoice));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("mappers")
    <E extends AuditableEntity, D extends AuditableModel> void mapsEveryValueToDomain(MapperCase<E, D> mapping) {
        D actual = context.getBean(mapping.type()).toDomain(mapping.entity().get());

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFieldsMatchingRegexes(HISTORY_LINK)
                .isEqualTo(mapping.domain().get());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("mappers")
    <E extends AuditableEntity, D extends AuditableModel> void mapsValuesToEntityExceptCreationAudit(
            MapperCase<E, D> mapping) {
        E actual = context.getBean(mapping.type()).toEntity(mapping.domain().get());

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFieldsMatchingRegexes(HISTORY_LINK, CREATION_AUDIT)
                .isEqualTo(mapping.entity().get());
        assertNoCreationAudit(actual);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("mappers")
    <E extends AuditableEntity, D extends AuditableModel> void mapsLists(MapperCase<E, D> mapping) {
        RepositoryMapper<E, D> mapper = context.getBean(mapping.type());

        assertThat(mapper.toDomains(List.of(mapping.entity().get())))
                .usingRecursiveComparison()
                .ignoringFieldsMatchingRegexes(HISTORY_LINK)
                .isEqualTo(List.of(mapping.domain().get()));
        assertThat(mapper.toEntities(List.of(mapping.domain().get())))
                .usingRecursiveComparison()
                .ignoringFieldsMatchingRegexes(HISTORY_LINK, CREATION_AUDIT)
                .isEqualTo(List.of(mapping.entity().get()));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("mappers")
    <E extends AuditableEntity, D extends AuditableModel> void mapsNullToNull(MapperCase<E, D> mapping) {
        RepositoryMapper<E, D> mapper = context.getBean(mapping.type());

        assertThat(mapper.toDomain(null)).isNull();
        assertThat(mapper.toEntity(null)).isNull();
        assertThat(mapper.toDomains(null)).isNull();
        assertThat(mapper.toEntities(null)).isNull();
    }

    @Test
    void ignoresCreationAuditOfRelationsWithTheirOwnMapper() {
        SpareEntity spare = context.getBean(SpareRepositoryMapper.class).toEntity(DomainFixtures.spare());
        RepairOrderEntity order = context.getBean(RepairInvoiceRepositoryMapper.class)
                .toEntity(DomainFixtures.invoice())
                .getOrder();

        Stream.of(
                        spare.getType(),
                        spare.getBrand(),
                        spare.getCountry(),
                        spare.getTransportType(),
                        order,
                        order.getTransport(),
                        order.getTransport().getType(),
                        order.getTransport().getBrand(),
                        order.getTransport().getCountry(),
                        order.getStatusHistory().get(0))
                .forEach(RepositoryMapperTest::assertNoCreationAudit);
    }

    @Test
    void mapsInvoiceToOrderButNotBackToInvoiceInDomain() {
        RepairInvoiceEntity invoice = EntityFixtures.invoice();
        invoice.getOrder().setInvoice(invoice);

        RepairInvoice actual =
                context.getBean(RepairInvoiceRepositoryMapper.class).toDomain(invoice);

        assertThat(actual.getOrder().getId()).isEqualTo(41L);
        assertThat(actual.getOrder().getInvoice()).isNull();
    }

    @Test
    void mapsInvoiceToOrderButNotBackToInvoiceInEntity() {
        RepairInvoice invoice = DomainFixtures.invoice();
        invoice.getOrder().setInvoice(invoice);

        RepairInvoiceEntity actual =
                context.getBean(RepairInvoiceRepositoryMapper.class).toEntity(invoice);

        assertThat(actual.getOrder().getId()).isEqualTo(41L);
        assertThat(actual.getOrder().getInvoice()).isNull();
    }

    private static void assertNoCreationAudit(AuditableEntity entity) {
        assertThat(entity.getCreatedAt()).isNull();
        assertThat(entity.getCreatedBy()).isNull();
    }

    private record MapperCase<E extends AuditableEntity, D extends AuditableModel>(
            Class<? extends RepositoryMapper<E, D>> type, Supplier<E> entity, Supplier<D> domain) {

        @Override
        public String toString() {
            return type.getSimpleName();
        }
    }
}

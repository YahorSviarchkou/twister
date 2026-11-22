package com.twister.persistence.mapper;

import com.twister.domain.repair.RepairOrderStatus;
import com.twister.domain.repair.RepairTaskItemStatus;
import com.twister.domain.repair.RepairTaskStatus;
import com.twister.persistence.entity.AuditableEntity;
import com.twister.persistence.entity.CustomerEntity;
import com.twister.persistence.entity.reference.CountryEntity;
import com.twister.persistence.entity.reference.ServiceTypeEntity;
import com.twister.persistence.entity.reference.SpareBrandEntity;
import com.twister.persistence.entity.reference.SpareEntity;
import com.twister.persistence.entity.reference.SpareTypeEntity;
import com.twister.persistence.entity.reference.TransportBrandEntity;
import com.twister.persistence.entity.reference.TransportEntity;
import com.twister.persistence.entity.reference.TransportTypeEntity;
import com.twister.persistence.entity.repair.RepairInvoiceEntity;
import com.twister.persistence.entity.repair.RepairOrderEntity;
import com.twister.persistence.entity.repair.RepairOrderStatusHistoryEntity;
import com.twister.persistence.entity.repair.RepairTaskEntity;
import com.twister.persistence.entity.repair.RepairTaskItemEntity;
import com.twister.persistence.entity.repair.RepairTaskItemStatusHistoryEntity;
import com.twister.persistence.entity.repair.RepairTaskStatusHistoryEntity;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/** Repeats the values of {@link DomainFixtures}, so that no mapper produces its own expected value. */
final class EntityFixtures {

    private EntityFixtures() {}

    static CountryEntity country() {
        return audited(
                11,
                CountryEntity.builder()
                        .id(11L)
                        .name("Germany")
                        .description("Origin")
                        .build());
    }

    static SpareTypeEntity spareType() {
        return audited(
                12,
                SpareTypeEntity.builder()
                        .id(12L)
                        .name("Brake")
                        .description("Braking parts")
                        .build());
    }

    static SpareBrandEntity spareBrand() {
        return audited(
                13,
                SpareBrandEntity.builder()
                        .id(13L)
                        .name("Magura")
                        .description("Hydraulics")
                        .build());
    }

    static TransportTypeEntity transportType() {
        return audited(
                14,
                TransportTypeEntity.builder()
                        .id(14L)
                        .name("Bicycle")
                        .description("Two wheels")
                        .build());
    }

    static TransportBrandEntity transportBrand() {
        return audited(
                15,
                TransportBrandEntity.builder()
                        .id(15L)
                        .name("Cube")
                        .description("Bike maker")
                        .build());
    }

    static ServiceTypeEntity serviceType() {
        return audited(
                16,
                ServiceTypeEntity.builder()
                        .id(16L)
                        .name("Bleeding")
                        .description("Brake bleeding")
                        .price(new BigDecimal("25.00"))
                        .build());
    }

    static SpareEntity spare() {
        return audited(
                21,
                SpareEntity.builder()
                        .id(21L)
                        .name("Brake pad")
                        .description("Front pad")
                        .model("MT-5")
                        .issueYear(2023)
                        .type(spareType())
                        .brand(spareBrand())
                        .country(country())
                        .sku("SP-21")
                        .url("https://example.com/spare")
                        .transportType(transportType())
                        .material("Ceramic")
                        .warehouseQuantity(7)
                        .price(new BigDecimal("19.90"))
                        .build());
    }

    static TransportEntity transport() {
        return audited(
                22,
                TransportEntity.builder()
                        .id(22L)
                        .name("City bike")
                        .description("Blue frame")
                        .model("Town Pro")
                        .issueYear(2022)
                        .type(transportType())
                        .brand(transportBrand())
                        .country(country())
                        .sku("TR-22")
                        .url("https://example.com/transport")
                        .build());
    }

    static CustomerEntity customer() {
        return audited(
                31,
                CustomerEntity.builder()
                        .id(31L)
                        .surname("Smirnov")
                        .name("Alex")
                        .patronymic("Ivanovich")
                        .phone("+1234567")
                        .email("alex@example.com")
                        .transportList(List.of(transport()))
                        .build());
    }

    static RepairOrderEntity order() {
        return audited(
                41,
                RepairOrderEntity.builder()
                        .id(41L)
                        .description("Brakes squeal")
                        .customer(customer())
                        .transport(transport())
                        .statusHistory(List.of(orderHistory()))
                        .task(task())
                        .build());
    }

    static RepairTaskEntity task() {
        return audited(
                42,
                RepairTaskEntity.builder()
                        .id(42L)
                        .items(List.of(taskItem()))
                        .statusHistory(List.of(taskHistory()))
                        .build());
    }

    static RepairTaskItemEntity taskItem() {
        return audited(
                43,
                RepairTaskItemEntity.builder()
                        .id(43L)
                        .serviceType(serviceType())
                        .spares(List.of(spare()))
                        .statusHistory(List.of(taskItemHistory()))
                        .build());
    }

    static RepairOrderStatusHistoryEntity orderHistory() {
        return audited(
                51,
                RepairOrderStatusHistoryEntity.builder()
                        .id(51L)
                        .status(RepairOrderStatus.ACCEPTED)
                        .build());
    }

    static RepairTaskStatusHistoryEntity taskHistory() {
        return audited(
                52,
                RepairTaskStatusHistoryEntity.builder()
                        .id(52L)
                        .status(RepairTaskStatus.DIAGNOSIS)
                        .build());
    }

    static RepairTaskItemStatusHistoryEntity taskItemHistory() {
        return audited(
                53,
                RepairTaskItemStatusHistoryEntity.builder()
                        .id(53L)
                        .status(RepairTaskItemStatus.WAITING_PARTS)
                        .build());
    }

    static RepairInvoiceEntity invoice() {
        return audited(
                61,
                RepairInvoiceEntity.builder()
                        .id(61L)
                        .description("Brake repair")
                        .order(order())
                        .diagnosticPrice(new BigDecimal("10.00"))
                        .sparePrice(new BigDecimal("19.90"))
                        .laborPrice(new BigDecimal("25.50"))
                        .build());
    }

    private static <T extends AuditableEntity> T audited(long id, T entity) {
        entity.setCreatedAt(Instant.parse("2026-01-01T10:00:00Z").plusSeconds(id));
        entity.setCreatedBy("creator-" + id);
        entity.setUpdatedAt(Instant.parse("2026-02-02T12:00:00Z").plusSeconds(id));
        entity.setUpdatedBy("editor-" + id);
        return entity;
    }
}

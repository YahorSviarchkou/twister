package com.twister.persistence.mapper;

import com.twister.domain.AuditableModel;
import com.twister.domain.Customer;
import com.twister.domain.reference.Country;
import com.twister.domain.reference.ServiceType;
import com.twister.domain.reference.Spare;
import com.twister.domain.reference.SpareBrand;
import com.twister.domain.reference.SpareType;
import com.twister.domain.reference.Transport;
import com.twister.domain.reference.TransportBrand;
import com.twister.domain.reference.TransportType;
import com.twister.domain.repair.RepairInvoice;
import com.twister.domain.repair.RepairOrder;
import com.twister.domain.repair.RepairOrderStatus;
import com.twister.domain.repair.RepairOrderStatusHistory;
import com.twister.domain.repair.RepairTask;
import com.twister.domain.repair.RepairTaskItem;
import com.twister.domain.repair.RepairTaskItemStatus;
import com.twister.domain.repair.RepairTaskItemStatusHistory;
import com.twister.domain.repair.RepairTaskStatus;
import com.twister.domain.repair.RepairTaskStatusHistory;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/** Repeats the values of {@link EntityFixtures}, so that no mapper produces its own expected value. */
final class DomainFixtures {

    private DomainFixtures() {}

    static Country country() {
        return audited(
                11,
                Country.builder().id(11L).name("Germany").description("Origin").build());
    }

    static SpareType spareType() {
        return audited(
                12,
                SpareType.builder()
                        .id(12L)
                        .name("Brake")
                        .description("Braking parts")
                        .build());
    }

    static SpareBrand spareBrand() {
        return audited(
                13,
                SpareBrand.builder()
                        .id(13L)
                        .name("Magura")
                        .description("Hydraulics")
                        .build());
    }

    static TransportType transportType() {
        return audited(
                14,
                TransportType.builder()
                        .id(14L)
                        .name("Bicycle")
                        .description("Two wheels")
                        .build());
    }

    static TransportBrand transportBrand() {
        return audited(
                15,
                TransportBrand.builder()
                        .id(15L)
                        .name("Cube")
                        .description("Bike maker")
                        .build());
    }

    static ServiceType serviceType() {
        return audited(
                16,
                ServiceType.builder()
                        .id(16L)
                        .name("Bleeding")
                        .description("Brake bleeding")
                        .price(new BigDecimal("25.00"))
                        .build());
    }

    static Spare spare() {
        return audited(
                21,
                Spare.builder()
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

    static Transport transport() {
        return audited(
                22,
                Transport.builder()
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

    static Customer customer() {
        return audited(
                31,
                Customer.builder()
                        .id(31L)
                        .surname("Smirnov")
                        .name("Alex")
                        .patronymic("Ivanovich")
                        .phone("+1234567")
                        .email("alex@example.com")
                        .transportList(List.of(transport()))
                        .build());
    }

    static RepairOrder order() {
        return audited(
                41,
                RepairOrder.builder()
                        .id(41L)
                        .description("Brakes squeal")
                        .customer(customer())
                        .transport(transport())
                        .statusHistory(List.of(orderHistory()))
                        .task(task())
                        .build());
    }

    static RepairTask task() {
        return audited(
                42,
                RepairTask.builder()
                        .id(42L)
                        .items(List.of(taskItem()))
                        .statusHistory(List.of(taskHistory()))
                        .build());
    }

    static RepairTaskItem taskItem() {
        return audited(
                43,
                RepairTaskItem.builder()
                        .id(43L)
                        .serviceType(serviceType())
                        .spares(List.of(spare()))
                        .statusHistory(List.of(taskItemHistory()))
                        .build());
    }

    static RepairOrderStatusHistory orderHistory() {
        return audited(
                51,
                RepairOrderStatusHistory.builder()
                        .id(51L)
                        .status(RepairOrderStatus.ACCEPTED)
                        .build());
    }

    static RepairTaskStatusHistory taskHistory() {
        return audited(
                52,
                RepairTaskStatusHistory.builder()
                        .id(52L)
                        .status(RepairTaskStatus.DIAGNOSIS)
                        .build());
    }

    static RepairTaskItemStatusHistory taskItemHistory() {
        return audited(
                53,
                RepairTaskItemStatusHistory.builder()
                        .id(53L)
                        .status(RepairTaskItemStatus.WAITING_PARTS)
                        .build());
    }

    static RepairInvoice invoice() {
        return audited(
                61,
                RepairInvoice.builder()
                        .id(61L)
                        .description("Brake repair")
                        .order(order())
                        .diagnosticPrice(new BigDecimal("10.00"))
                        .sparePrice(new BigDecimal("19.90"))
                        .laborPrice(new BigDecimal("25.50"))
                        .build());
    }

    private static <T extends AuditableModel> T audited(long id, T model) {
        model.setCreatedAt(Instant.parse("2026-01-01T10:00:00Z").plusSeconds(id));
        model.setCreatedBy("creator-" + id);
        model.setUpdatedAt(Instant.parse("2026-02-02T12:00:00Z").plusSeconds(id));
        model.setUpdatedBy("editor-" + id);
        return model;
    }
}

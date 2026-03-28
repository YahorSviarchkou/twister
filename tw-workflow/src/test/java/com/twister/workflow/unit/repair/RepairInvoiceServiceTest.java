package com.twister.workflow.unit.repair;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.twister.domain.reference.ServiceType;
import com.twister.domain.reference.Spare;
import com.twister.domain.repair.RepairInvoice;
import com.twister.domain.repair.RepairOrder;
import com.twister.domain.repair.RepairTask;
import com.twister.domain.repair.RepairTaskItem;
import com.twister.workflow.dao.repair.RepairInvoiceDao;
import com.twister.workflow.service.repair.RepairInvoiceService;
import com.twister.workflow.service.repair.RepairOrderService;
import com.twister.workflow.service.repair.RepairTaskItemService;
import com.twister.workflow.service.repair.RepairTaskService;
import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RepairInvoiceServiceTest {

    private static final Long INVOICE_ID = 1L;
    private static final Long ORDER_ID = 10L;
    private static final Long TASK_ID = 100L;
    private static final Long ITEM_1_ID = 1000L;
    private static final Long ITEM_2_ID = 2000L;
    private static final Long NOT_EXISTING = Long.MAX_VALUE;

    @Mock
    private RepairInvoiceDao invoiceDao;

    @Mock
    private RepairOrderService repairOrderService;

    @Mock
    private RepairTaskService repairTaskService;

    @Mock
    private RepairTaskItemService repairTaskItemService;

    @InjectMocks
    private RepairInvoiceService invoiceService;

    @Test
    void testGetRepairInvoiceByIdWhenIdExists() {
        // Given
        RepairInvoice invoice = RepairInvoice.builder().id(INVOICE_ID).build();
        // When
        when(invoiceDao.findById(INVOICE_ID)).thenReturn(Optional.of(invoice));
        // Then
        RepairInvoice result = invoiceService.getRepairInvoiceById(INVOICE_ID);

        assertNotNull(result);
        assertEquals(INVOICE_ID, result.getId());

        verify(invoiceDao, times(1)).findById(anyLong());
    }

    @Test
    void testGetRepairInvoiceByIdWhenIdNotExists() {
        // When
        when(invoiceDao.findById(NOT_EXISTING)).thenReturn(Optional.empty());
        // Then
        assertThrows(NoSuchElementException.class, () -> invoiceService.getRepairInvoiceById(NOT_EXISTING));
    }

    @Test
    void testGetRepairInvoiceByIdWhenOrderIdExists() {
        // Given
        RepairInvoice invoice = RepairInvoice.builder().id(INVOICE_ID).build();
        // When
        when(invoiceDao.findRepairInvoiceByOrderId(ORDER_ID)).thenReturn(Optional.of(invoice));
        // Then
        RepairInvoice result = invoiceService.getRepairInvoiceByOrderId(ORDER_ID);

        assertNotNull(result);
        assertEquals(INVOICE_ID, result.getId());
    }

    @Test
    void testGetRepairInvoiceByIdWhenOrderIdNotExists() {
        // When
        when(invoiceDao.findRepairInvoiceByOrderId(NOT_EXISTING)).thenReturn(Optional.empty());
        // Then
        assertThrows(NoSuchElementException.class, () -> invoiceService.getRepairInvoiceByOrderId(NOT_EXISTING));
    }

    @Test
    void testGenerateRepairInvoiceWhenAllDataExist() {
        // Given
        String description = "SOME DESCRIPTION";
        BigDecimal diagnosticPrice = new BigDecimal("10.00");
        BigDecimal sparePrice1 = new BigDecimal("100.00");
        BigDecimal sparePrice2 = new BigDecimal("200.00");
        BigDecimal sparePrice3 = new BigDecimal("250.50");
        BigDecimal sparePrice4 = new BigDecimal("150.50");
        BigDecimal servicePrice1 = new BigDecimal("1555.50");
        BigDecimal servicePrice2 = new BigDecimal("444.50");
        BigDecimal globalSparePrice =
                sparePrice1.add(sparePrice2).add(sparePrice3).add(sparePrice4);
        BigDecimal globalServicePrice = servicePrice1.add(servicePrice2);
        List<Spare> spares1 = List.of(
                Spare.builder().price(sparePrice1).build(),
                Spare.builder().price(sparePrice3).build());
        List<Spare> spares2 = List.of(
                Spare.builder().price(sparePrice2).build(),
                Spare.builder().price(sparePrice4).build());
        ServiceType serviceType1 = ServiceType.builder().price(servicePrice1).build();
        ServiceType serviceType2 = ServiceType.builder().price(servicePrice2).build();
        RepairTaskItem item1 = RepairTaskItem.builder()
                .id(ITEM_1_ID)
                .spares(spares1)
                .serviceType(serviceType1)
                .build();
        RepairTaskItem item2 = RepairTaskItem.builder()
                .id(ITEM_2_ID)
                .spares(spares2)
                .serviceType(serviceType2)
                .build();
        RepairTask task = RepairTask.builder().id(TASK_ID).build();
        RepairOrder order = RepairOrder.builder().id(ORDER_ID).build();

        // When
        when(repairOrderService.getRepairOrderById(ORDER_ID)).thenReturn(order);
        when(repairTaskService.getRepairTaskByOrderId(ORDER_ID)).thenReturn(task);
        when(repairTaskItemService.getAllByTaskId(TASK_ID)).thenReturn(List.of(item1, item2));
        when(invoiceDao.save(any(RepairInvoice.class))).thenAnswer(inv -> inv.getArgument(0));

        // Then
        RepairInvoice result = invoiceService.generateRepairInvoice(diagnosticPrice, description, ORDER_ID);

        assertNotNull(result);
        assertEquals(order, result.getOrder());
        assertEquals(description, result.getDescription());
        assertEquals(globalSparePrice, result.getSparePrice());
        assertEquals(globalServicePrice, result.getLaborPrice());
        assertEquals(diagnosticPrice, result.getDiagnosticPrice());
    }

    @Test
    void testGenerateRepairInvoiceWhenNoPrices() {
        // Given
        String description = "SOME DESCRIPTION";
        ServiceType serviceType1 = ServiceType.builder().build();
        RepairTaskItem item1 = RepairTaskItem.builder()
                .id(ITEM_1_ID)
                .spares(List.of())
                .serviceType(serviceType1)
                .build();
        RepairTask task = RepairTask.builder().id(TASK_ID).build();
        RepairOrder order = RepairOrder.builder().id(ORDER_ID).build();

        // When
        when(repairOrderService.getRepairOrderById(ORDER_ID)).thenReturn(order);
        when(repairTaskService.getRepairTaskByOrderId(ORDER_ID)).thenReturn(task);
        when(repairTaskItemService.getAllByTaskId(TASK_ID)).thenReturn(List.of(item1));
        when(invoiceDao.save(any(RepairInvoice.class))).thenAnswer(inv -> inv.getArgument(0));

        // Then
        RepairInvoice result = invoiceService.generateRepairInvoice(null, description, ORDER_ID);

        assertNotNull(result);
        assertEquals(order, result.getOrder());
        assertEquals(description, result.getDescription());
        assertEquals(BigDecimal.ZERO, result.getSparePrice());
        assertEquals(BigDecimal.ZERO, result.getLaborPrice());
        assertEquals(BigDecimal.ZERO, result.getDiagnosticPrice());
    }
}

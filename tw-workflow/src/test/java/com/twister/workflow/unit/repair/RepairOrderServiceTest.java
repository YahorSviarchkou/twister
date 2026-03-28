package com.twister.workflow.unit.repair;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.twister.domain.Customer;
import com.twister.domain.reference.Transport;
import com.twister.domain.repair.RepairOrder;
import com.twister.domain.repair.RepairOrderStatus;
import com.twister.workflow.dao.repair.RepairOrderDao;
import com.twister.workflow.service.CustomerService;
import com.twister.workflow.service.reference.TransportService;
import com.twister.workflow.service.repair.RepairOrderService;
import com.twister.workflow.service.repair.RepairOrderStatusHistoryService;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RepairOrderServiceTest {

    private static final Long ORDER_ID = 1L;
    private static final Long CUSTOMER_ID = 10L;
    private static final Long TRANSPORT_ID = 100L;
    private static final Long NOT_EXISTING = Long.MAX_VALUE;

    @Mock
    private RepairOrderDao repairOrderDao;

    @Mock
    private CustomerService customerService;

    @Mock
    private TransportService transportService;

    @Mock
    private RepairOrderStatusHistoryService historyService;

    @InjectMocks
    private RepairOrderService orderService;

    @Captor
    private ArgumentCaptor<RepairOrder> orderCaptor;

    @Captor
    private ArgumentCaptor<RepairOrderStatus> orderStatusCaptor;

    @Test
    void testGetRepairOrderByIdWhenIdExists() {
        // Given
        RepairOrder order = RepairOrder.builder().id(ORDER_ID).build();
        // When
        when(repairOrderDao.findById(ORDER_ID)).thenReturn(Optional.of(order));
        // Then
        RepairOrder result = orderService.getRepairOrderById(ORDER_ID);

        assertNotNull(result);
        assertEquals(ORDER_ID, result.getId());

        verify(repairOrderDao, times(1)).findById(anyLong());
    }

    @Test
    void testGetRepairOrderByIdWhenIdNotExists() {
        // When
        when(repairOrderDao.findById(NOT_EXISTING)).thenReturn(Optional.empty());
        // Then
        assertThrows(NoSuchElementException.class, () -> orderService.getRepairOrderById(NOT_EXISTING));
    }

    @Test
    void testGetRepairOrderByIdWhenTaskIdExists() {
        // Given
        RepairOrder order = RepairOrder.builder().id(ORDER_ID).build();
        // When
        when(repairOrderDao.findRepairOrderByTaskId(0L)).thenReturn(Optional.of(order));
        // Then
        RepairOrder result = orderService.getRepairOrderByTaskId(0L);

        assertNotNull(result);
        assertEquals(ORDER_ID, result.getId());

        verify(repairOrderDao, times(1)).findRepairOrderByTaskId(anyLong());
    }

    @Test
    void testGetRepairOrderByIdWhenTaskIdNotExists() {
        // When
        when(repairOrderDao.findRepairOrderByTaskId(NOT_EXISTING)).thenReturn(Optional.empty());
        // Then
        assertThrows(NoSuchElementException.class, () -> orderService.getRepairOrderByTaskId(NOT_EXISTING));
    }

    @Test
    void testCreateRepairOrderWhenAllDataExist() {
        // Given
        Customer dbCustomer =
                Customer.builder().id(CUSTOMER_ID).name("CUSTOMER_NAME").build();
        Transport dbTransport =
                Transport.builder().id(TRANSPORT_ID).name("TRANSPORT_NAME").build();
        RepairOrder order = RepairOrder.builder()
                .customer(Customer.builder().id(CUSTOMER_ID).build())
                .transport(Transport.builder().id(TRANSPORT_ID).build())
                .build();
        // When
        when(customerService.getCustomerById(CUSTOMER_ID)).thenReturn(dbCustomer);
        when(transportService.getTransportById(TRANSPORT_ID)).thenReturn(dbTransport);
        when(repairOrderDao.save(any(RepairOrder.class))).thenAnswer(inv -> inv.getArgument(0));
        // Then
        orderService.create(order);

        verify(repairOrderDao, times(1)).save(orderCaptor.capture());
        verify(historyService, times(1)).updateWorkflowStatus(nullable(Long.class), orderStatusCaptor.capture());

        RepairOrder result = orderCaptor.getValue();
        RepairOrderStatus resultStatus = orderStatusCaptor.getValue();

        assertEquals(dbCustomer, result.getCustomer());
        assertEquals(dbTransport, result.getTransport());
        assertEquals(RepairOrderStatus.OPEN, resultStatus);
    }

    @Test
    void testCreateRepairOrderWhenDataIsNull() {
        // Then
        assertThrows(IllegalArgumentException.class, () -> orderService.create(null));
    }

    @Test
    void testCreateRepairOrderWhenCustomerIsNull() {
        // Given
        Transport transport = Transport.builder().id(TRANSPORT_ID).build();
        RepairOrder order = RepairOrder.builder().transport(transport).build();
        // Then
        assertThrows(IllegalStateException.class, () -> orderService.create(order));
    }

    @Test
    void testCreateRepairOrderWhenTransportIsNull() {
        // Given
        Customer customer = Customer.builder().id(CUSTOMER_ID).build();
        RepairOrder order = RepairOrder.builder().customer(customer).build();
        // Then
        assertThrows(IllegalStateException.class, () -> orderService.create(order));
    }
}

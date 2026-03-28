package com.twister.workflow.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.twister.domain.Customer;
import com.twister.domain.reference.Transport;
import com.twister.workflow.dao.CustomerDao;
import com.twister.workflow.service.CustomerService;
import com.twister.workflow.service.reference.TransportService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    private static final Long ID = 1L;
    private static final Long TRANSPORT_ID = 10L;

    @Mock
    private CustomerDao customerDao;

    @Mock
    private TransportService transportService;

    @InjectMocks
    private CustomerService customerService;

    @Captor
    private ArgumentCaptor<Customer> customerCaptor;

    @Test
    void testCreateCustomerWhenAllDataValid() {
        // Given
        Customer customer = Customer.builder()
                .name("John")
                .surname("Doe")
                .patronymic("Jr.")
                .phone("12345")
                .build();
        // When
        when(customerDao.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));
        // Then
        customerService.create(customer);

        verify(customerDao, times(1)).save(customerCaptor.capture());
        Customer result = customerCaptor.getValue();
        assertEquals(customer, result);
    }

    @Test
    void testCreateCustomerWhenDataIsNull() {
        // Then
        assertThrows(IllegalArgumentException.class, () -> customerService.create(null));
    }

    @Test
    void testCreateCustomerWhenDataIsEmpty() {
        // Given
        Customer customer = Customer.builder().build();
        // Then
        assertThrows(IllegalStateException.class, () -> customerService.create(customer));
    }

    @Test
    void testCreateCustomerWhenTransportNotNull() {
        // Given
        Customer customer = Customer.builder()
                .transportList(List.of(Transport.builder().build()))
                .build();
        // Then
        assertThrows(IllegalStateException.class, () -> customerService.create(customer));
    }

    @Test
    void testUpdateCustomerWhenDataIsValid() {
        // Given
        Customer customer =
                Customer.builder().id(ID).name("John").surname("Doe").build();
        Customer dbCustomer = Customer.builder()
                .id(ID)
                .name("Test")
                .surname("Test")
                .patronymic("Jr.")
                .phone("12345")
                .build();
        // When
        when(customerDao.findById(ID)).thenReturn(Optional.of(dbCustomer));
        when(customerDao.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));
        // Then
        customerService.update(customer);

        verify(customerDao, times(1)).save(customerCaptor.capture());
        Customer result = customerCaptor.getValue();

        assertEquals(ID, result.getId());
        assertEquals(customer.getName(), result.getName());
        assertEquals(customer.getSurname(), result.getSurname());
        assertEquals(dbCustomer.getPatronymic(), result.getPatronymic());
        assertEquals(dbCustomer.getPhone(), result.getPhone());
    }

    @Test
    void testUpdateCustomerWhenDataIsNull() {
        // Then
        assertThrows(IllegalArgumentException.class, () -> customerService.update(null));
    }

    @Test
    void testUpdateCustomerWhenIdIsNull() {
        // Given
        Customer customer = Customer.builder().build();
        // Then
        assertThrows(IllegalArgumentException.class, () -> customerService.update(customer));
    }

    @Test
    void testUpdateCustomerWhenTransportNotNull() {
        // Given
        Customer customer = Customer.builder()
                .id(ID)
                .transportList(List.of(Transport.builder().build()))
                .build();
        // Then
        assertThrows(IllegalStateException.class, () -> customerService.update(customer));
    }

    @Test
    void testAddTransportToCustomerWhenDataIsValid() {
        // Given
        Customer customer =
                Customer.builder().id(ID).transportList(new ArrayList<>()).build();
        Transport transport = Transport.builder().id(TRANSPORT_ID).build();
        // When
        when(customerDao.findById(ID)).thenReturn(Optional.of(customer));
        when(transportService.getTransportById(TRANSPORT_ID)).thenReturn(transport);
        when(customerDao.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));
        // Then
        customerService.addTransport(ID, TRANSPORT_ID);

        verify(customerDao, times(1)).save(customerCaptor.capture());
        Customer result = customerCaptor.getValue();

        assertEquals(1, result.getTransportList().size());
        assertEquals(transport, result.getTransportList().getFirst());
    }

    @Test
    void testAddTransportToCustomerWhenTransportAlreadyExist() {
        // Given
        Transport transport = Transport.builder().id(TRANSPORT_ID).build();
        Customer customer =
                Customer.builder().id(ID).transportList(List.of(transport)).build();
        // When
        when(customerDao.findById(ID)).thenReturn(Optional.of(customer));
        when(transportService.getTransportById(TRANSPORT_ID)).thenReturn(transport);
        // Then
        assertThrows(IllegalStateException.class, () -> customerService.addTransport(ID, TRANSPORT_ID));

        verify(customerDao, times(0)).save(any(Customer.class));
    }

    @Test
    void testAddTransportToCustomerWhenCustomerIdIsNull() {
        // Then
        assertThrows(IllegalArgumentException.class, () -> customerService.addTransport(null, TRANSPORT_ID));
    }

    @Test
    void testAddTransportToCustomerWhenTransportIdIsNull() {
        // Then
        assertThrows(IllegalArgumentException.class, () -> customerService.addTransport(ID, null));
    }
}

package com.twister.workflow.service;

import com.twister.domain.Customer;
import com.twister.domain.reference.Transport;
import com.twister.repository.CustomerRepository;
import com.twister.payload.CustomerFilter;
import com.twister.repository.specification.CustomerSpecification;
import com.twister.workflow.service.reference.TransportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final TransportService transportService;

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
            .orElseThrow(() ->
                new NoSuchElementException("Customer not found by id: " + id)
            );
    }

    public Page<Customer> getAllCustomers(CustomerFilter filter, Pageable pageable) {
        Specification<Customer> specification = CustomerSpecification.search(filter);
        return customerRepository.findAll(specification, pageable);
    }

    public Page<Customer> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }

    public void create(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer not exist for creating");
        }
        if (customer.getTransportList() != null && !customer.getTransportList().isEmpty()) {
            throw new IllegalStateException("Customer must not contains transport on creating");
        }

        log.info("Creating customer");
        if (customer.getName() == null || customer.getName().isBlank()
            || customer.getSurname() == null || customer.getSurname().isBlank()
            || customer.getPatronymic() == null || customer.getPatronymic().isBlank()) {
            throw new IllegalStateException("Customer name, surname, patronymic must exist");
        }

        customer.setId(null);
        Customer dbCustomer = customerRepository.save(customer);
        log.info("Customer successfully created with id: {}", dbCustomer.getId());
    }

    public void update(Customer customer) {
        if (customer == null || customer.getId() == null) {
            throw new IllegalArgumentException("Customer not exist for updating");
        }
        if (customer.getTransportList() != null && !customer.getTransportList().isEmpty()) {
            throw new IllegalStateException("Customer must not contains transport on updating");
        }

        log.info("Updating customer with id: {}", customer.getId());
        Customer dbCustomer = getCustomerById(customer.getId());

        Optional.ofNullable(customer.getSurname()).ifPresent(dbCustomer::setSurname);
        Optional.ofNullable(customer.getName()).ifPresent(dbCustomer::setName);
        Optional.ofNullable(customer.getPatronymic()).ifPresent(dbCustomer::setPatronymic);
        Optional.ofNullable(customer.getPhone()).ifPresent(dbCustomer::setPhone);

        customerRepository.save(dbCustomer);
        log.info("Customer with id: {}, successfully updated", customer.getId());
    }

    public void addTransport(Long customerId, Long transportId) {
        if (customerId == null || transportId == null) {
            throw new IllegalArgumentException("customerId and transportId must exist");
        }

        Customer customer = getCustomerById(customerId);
        Transport transport = transportService.getTransportById(transportId);

        log.info("Adding transport[id: {}] to customer[id: {}]", transportId, customerId);
        if (customer.getTransportList().contains(transport)) {
            throw new IllegalStateException(
                "Customer[id: %s] already has transport[id: %s]".formatted(customerId, transportId)
            );
        }

        customer.getTransportList().add(transport);
        customerRepository.save(customer);
        log.info("Transport [id: {}] successfully added to customer[id: {}]", transportId, customerId);
    }

    private void deleteCustomer(Long customerId) {
        if (customerId == null) {
            throw new IllegalArgumentException("CustomerId must be exist for deleting");
        }

        log.info("Deleting customer by id: {}", customerId);
        customerRepository.deleteById(customerId);
        log.info("Customer with id: {}, was successfully deleted", customerId);
    }
}

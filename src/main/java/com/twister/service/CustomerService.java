package com.twister.service;

import com.twister.entity.Customer;
import com.twister.repository.CustomerRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerService {

    CustomerRepository customerRepository;

    public List<Customer> findAllClients(int pageNumber, int pageSize, Sort sort) {
        var pageRequest = PageRequest.of(pageNumber, pageSize, sort);
        var clients = customerRepository.findAll(pageRequest);

        return clients.isEmpty()
                ? Collections.emptyList()
                : clients.toList();
    }

    public Optional<Customer> findClientById(Long id) {
        return customerRepository.findById(id);
    }

    public boolean isClientExists(Long id) {
        return customerRepository.existsById(id);
    }

    public Customer createCustomer(Customer customer) {
        if (Objects.isNull(customer.getId())) {
            var saved = customerRepository.save(customer);
            log.info("Created client with id: {}, name: {}", saved.getId(), saved.getPatronymic());
            return saved;
        }
        log.error("Can't create a client with non-null id");
        throw new IllegalStateException("Can't create a client with non-null id");
    }

    public Customer updateCustomer(Customer oldCustomer, Customer newCustomer) {
//        if (Objects.isNull(client.getId())) {
//            var saved = clientRepository.save(client);
//            log.info("Created client with id: {}, name: {}", saved.getId(), saved.getFullName());
//        }
//        log.error("Can't update a client with non-null id");
//        throw new IllegalStateException("Can't update a client with non-null id");
        return null;
    }

    public void deleteClient(Long id) {
        log.info("Delete client by id: {}", id);
        customerRepository.deleteById(id);
        log.info("Client with id: {} was deleted", id);
    }
}

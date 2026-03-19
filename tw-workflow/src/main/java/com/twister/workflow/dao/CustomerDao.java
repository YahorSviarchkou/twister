package com.twister.workflow.dao;

import com.twister.domain.Customer;
import com.twister.payload.CustomerFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CustomerDao {

    Optional<Customer> findById(Long id);

    Page<Customer> findAll(CustomerFilter filter, Pageable pageable);

    Page<Customer> findAll(Pageable pageable);

    Customer save(Customer customer);

    void deleteById(Long customerId);
}

package com.twister.workflow.dao;

import com.twister.domain.Customer;
import com.twister.payload.CustomerFilter;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerDao {

    Optional<Customer> findById(Long id);

    Page<Customer> findAll(CustomerFilter filter, Pageable pageable);

    Page<Customer> findAll(Pageable pageable);

    Customer save(Customer customer);

    void deleteById(Long customerId);
}

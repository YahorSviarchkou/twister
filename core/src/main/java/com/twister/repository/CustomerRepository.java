package com.twister.repository;

import com.twister.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends
    JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {
}

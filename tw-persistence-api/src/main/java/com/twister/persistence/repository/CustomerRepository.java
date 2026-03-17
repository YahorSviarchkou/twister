package com.twister.persistence.repository;

import com.twister.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends
    JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {
}

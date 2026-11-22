package com.twister.desktop.app.repository;

import com.twister.domain.Customer;
import com.twister.payload.CustomerFilter;
import com.twister.persistence.repository.CustomerRepository;
import com.twister.workflow.dao.CustomerDao;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerDaoImpl implements CustomerDao {

    private final CustomerRepository customerRepository;

    @Override
    public Optional<Customer> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Page<Customer> findAll(CustomerFilter filter, Pageable pageable) {
        return null;
    }

    @Override
    public Page<Customer> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Customer save(Customer customer) {
        return null;
    }

    @Override
    public void deleteById(Long customerId) {}
}

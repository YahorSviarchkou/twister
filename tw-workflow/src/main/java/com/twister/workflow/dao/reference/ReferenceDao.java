package com.twister.workflow.dao.reference;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReferenceDao<T> {

    Optional<T> findById(Long id);

    Optional<T> findByName(String name);

    Page<T> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<T> findAll(Pageable pageable);

    List<T> findByIdIn(Set<Long> ids);

    T save(T reference);

    void deleteById(Long id);
}

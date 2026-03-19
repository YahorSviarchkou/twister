package com.twister.workflow.dao.reference;

import com.twister.domain.reference.Spare;
import com.twister.payload.SpareFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SpareDao {

    Optional<Spare> findById(Long id);

    List<Spare> findByIdIn(Set<Long> ids);

    Page<Spare> findAll(SpareFilter filter, Pageable pageable);

    Page<Spare> findAll(Pageable pageable);

    Spare save(Spare spare);
}

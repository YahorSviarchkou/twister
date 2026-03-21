package com.twister.workflow.dao.reference;

import com.twister.domain.reference.Spare;
import com.twister.payload.SpareFilter;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SpareDao {

    Optional<Spare> findById(Long id);

    Optional<Spare> findByName(String name);

    List<Spare> findByIdIn(Set<Long> ids);

    Page<Spare> findAll(SpareFilter filter, Pageable pageable);

    Page<Spare> findAll(Pageable pageable);

    Spare save(Spare spare);
}

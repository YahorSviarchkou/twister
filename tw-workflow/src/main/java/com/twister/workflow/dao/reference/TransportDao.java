package com.twister.workflow.dao.reference;

import com.twister.domain.reference.Transport;
import com.twister.payload.TransportFilter;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransportDao {

    Optional<Transport> findById(Long id);

    Page<Transport> findAll(TransportFilter filter, Pageable pageable);

    Page<Transport> findAll(Pageable pageable);

    Transport save(Transport transport);
}

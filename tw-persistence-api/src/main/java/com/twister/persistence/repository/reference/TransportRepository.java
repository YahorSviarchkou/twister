package com.twister.persistence.repository.reference;

import com.twister.domain.reference.Transport;
import com.twister.repository.reference.CompositeReferenceRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransportRepository extends CompositeReferenceRepository<Transport> {
}

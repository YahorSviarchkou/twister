package com.twister.repository.reference;

import com.twister.model.reference.Transport;
import org.springframework.stereotype.Repository;

@Repository
public interface TransportRepository extends CompositeReferenceRepository<Transport> {
}

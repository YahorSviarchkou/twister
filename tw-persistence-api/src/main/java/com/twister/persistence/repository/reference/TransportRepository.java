package com.twister.persistence.repository.reference;

import com.twister.persistence.entity.reference.TransportEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface TransportRepository extends CompositeReferenceRepository<TransportEntity> {}

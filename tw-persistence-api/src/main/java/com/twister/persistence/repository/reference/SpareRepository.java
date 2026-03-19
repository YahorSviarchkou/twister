package com.twister.persistence.repository.reference;

import com.twister.persistence.entity.reference.SpareEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface SpareRepository extends CompositeReferenceRepository<SpareEntity> {
}

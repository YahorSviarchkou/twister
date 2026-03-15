package com.twister.repository.reference;

import com.twister.model.reference.Spare;
import org.springframework.stereotype.Repository;

@Repository
public interface SpareRepository extends CompositeReferenceRepository<Spare> {
}

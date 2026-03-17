package com.twister.repository.reference;

import com.twister.domain.reference.Spare;
import org.springframework.stereotype.Repository;

@Repository
public interface SpareRepository extends CompositeReferenceRepository<Spare> {
}

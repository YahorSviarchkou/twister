package com.twister.persistence.repository.reference;

import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface CompositeReferenceRepository<T> extends JpaRepository<T, Long>, JpaSpecificationExecutor<T> {

    List<T> findByIdIn(Set<Long> ids);
}

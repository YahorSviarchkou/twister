package com.twister.repository.reference;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Set;

@NoRepositoryBean
public interface CompositeReferenceRepository<T> extends
    JpaRepository<T, Long>, JpaSpecificationExecutor<T> {

    List<T> findByIdIn(Set<Long> ids);
}

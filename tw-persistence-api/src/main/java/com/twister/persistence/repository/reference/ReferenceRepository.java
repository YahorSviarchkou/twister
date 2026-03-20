package com.twister.persistence.repository.reference;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ReferenceRepository<T> extends JpaRepository<T, Long> {

    Optional<T> findByName(String name);

    Page<T> findByNameContainingIgnoreCase(String name, Pageable pageable);

    List<T> findByIdIn(Set<Long> ids);
}

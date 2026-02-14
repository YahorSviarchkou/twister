package com.twister.repository;

import com.twister.entity.Spare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface SpareRepository extends JpaRepository<Spare, Long> {

    boolean existsByTitle(String title);

    Set<Spare> findAllSparesByIdIn(Set<Long> ids);
}

package com.twister.repository;

import com.twister.entity.OperationSpares;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationSparesRepository extends JpaRepository<OperationSpares, Long> {
}

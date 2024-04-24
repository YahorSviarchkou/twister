package com.twister.repository;

import com.twister.entity.TaskOperations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskOperationsRepository extends JpaRepository<TaskOperations, Long> {
}

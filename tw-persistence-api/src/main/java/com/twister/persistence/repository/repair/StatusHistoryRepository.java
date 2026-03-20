package com.twister.persistence.repository.repair;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface StatusHistoryRepository<T> extends JpaRepository<T, Long> {

    Optional<T> findFirstByWorkflowItem_IdOrderByCreatedAtDesc(Long workflowItemId);

    List<T> findByWorkflowItem_Id(Long workflowItemId);
}

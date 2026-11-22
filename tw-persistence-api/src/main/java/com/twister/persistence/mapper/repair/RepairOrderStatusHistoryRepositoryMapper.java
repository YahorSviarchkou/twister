package com.twister.persistence.mapper.repair;

import com.twister.domain.repair.RepairOrderStatusHistory;
import com.twister.persistence.entity.repair.RepairOrderStatusHistoryEntity;
import com.twister.persistence.mapper.AuditableRepositoryMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RepairOrderStatusHistoryRepositoryMapper
        extends AuditableRepositoryMapper<RepairOrderStatusHistoryEntity, RepairOrderStatusHistory> {}

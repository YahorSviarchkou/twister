package com.twister.persistence.mapper.repair;

import com.twister.domain.repair.RepairOrder;
import com.twister.persistence.entity.repair.RepairOrderEntity;
import com.twister.persistence.mapper.AuditableRepositoryMapper;
import com.twister.persistence.mapper.CustomerRepositoryMapper;
import com.twister.persistence.mapper.reference.TransportRepositoryMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        // TODO(discuss): customer and task mappers are empty, so their generated mappings copy createdAt/createdBy
        uses = {
            CustomerRepositoryMapper.class,
            TransportRepositoryMapper.class,
            RepairOrderStatusHistoryRepositoryMapper.class,
            RepairTaskRepositoryMapper.class
        })
public interface RepairOrderRepositoryMapper extends AuditableRepositoryMapper<RepairOrderEntity, RepairOrder> {

    // Overriding does not inherit @Mapping, so the auditing ignores are repeated.
    // TODO(discuss): populated order/task and task/item back-references recurse infinitely, also in toDomain
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "invoice", ignore = true)
    @Override
    RepairOrderEntity toEntity(RepairOrder domain);

    // TODO(discuss): order.invoice is no longer mapped (the invoice owns the FK); decide who loads it for an order
    @Mapping(target = "invoice", ignore = true)
    @Override
    RepairOrder toDomain(RepairOrderEntity entity);
}

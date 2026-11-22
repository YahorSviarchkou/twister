package com.twister.persistence.mapper.repair;

import com.twister.domain.repair.RepairOrder;
import com.twister.persistence.entity.repair.RepairOrderEntity;
import com.twister.persistence.mapper.AuditableRepositoryMapper;
import com.twister.persistence.mapper.CustomerRepositoryMapper;
import com.twister.persistence.mapper.reference.TransportRepositoryMapper;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        uses = {
            CustomerRepositoryMapper.class,
            TransportRepositoryMapper.class,
            RepairOrderStatusHistoryRepositoryMapper.class,
            RepairTaskRepositoryMapper.class,
            RepairInvoiceRepositoryMapper.class
        })
public interface RepairOrderRepositoryMapper extends AuditableRepositoryMapper<RepairOrderEntity, RepairOrder> {}

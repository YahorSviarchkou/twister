package com.twister.persistence.mapper.repair;

import com.twister.domain.repair.RepairInvoice;
import com.twister.persistence.entity.repair.RepairInvoiceEntity;
import com.twister.persistence.mapper.AuditableRepositoryMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = RepairOrderRepositoryMapper.class)
public interface RepairInvoiceRepositoryMapper extends AuditableRepositoryMapper<RepairInvoiceEntity, RepairInvoice> {}

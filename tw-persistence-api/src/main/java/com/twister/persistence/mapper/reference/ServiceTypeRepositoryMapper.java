package com.twister.persistence.mapper.reference;

import com.twister.domain.reference.ServiceType;
import com.twister.persistence.entity.reference.ServiceTypeEntity;
import com.twister.persistence.mapper.AuditableRepositoryMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ServiceTypeRepositoryMapper extends AuditableRepositoryMapper<ServiceTypeEntity, ServiceType> {}

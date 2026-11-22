package com.twister.persistence.mapper.reference;

import com.twister.domain.reference.TransportType;
import com.twister.persistence.entity.reference.TransportTypeEntity;
import com.twister.persistence.mapper.AuditableRepositoryMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransportTypeRepositoryMapper extends AuditableRepositoryMapper<TransportTypeEntity, TransportType> {}

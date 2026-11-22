package com.twister.persistence.mapper.reference;

import com.twister.domain.reference.TransportBrand;
import com.twister.persistence.entity.reference.TransportBrandEntity;
import com.twister.persistence.mapper.AuditableRepositoryMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransportBrandRepositoryMapper
        extends AuditableRepositoryMapper<TransportBrandEntity, TransportBrand> {}

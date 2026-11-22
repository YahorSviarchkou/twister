package com.twister.persistence.mapper.reference;

import com.twister.domain.reference.SpareBrand;
import com.twister.persistence.entity.reference.SpareBrandEntity;
import com.twister.persistence.mapper.AuditableRepositoryMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SpareBrandRepositoryMapper extends AuditableRepositoryMapper<SpareBrandEntity, SpareBrand> {}

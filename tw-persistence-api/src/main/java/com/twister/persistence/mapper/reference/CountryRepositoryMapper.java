package com.twister.persistence.mapper.reference;

import com.twister.domain.reference.Country;
import com.twister.persistence.entity.reference.CountryEntity;
import com.twister.persistence.mapper.AuditableRepositoryMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CountryRepositoryMapper extends AuditableRepositoryMapper<CountryEntity, Country> {}

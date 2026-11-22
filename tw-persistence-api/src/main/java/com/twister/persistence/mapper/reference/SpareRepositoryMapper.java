package com.twister.persistence.mapper.reference;

import com.twister.domain.reference.Spare;
import com.twister.persistence.entity.reference.SpareEntity;
import com.twister.persistence.mapper.AuditableRepositoryMapper;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        uses = {
            CountryRepositoryMapper.class,
            SpareTypeRepositoryMapper.class,
            SpareBrandRepositoryMapper.class,
            TransportTypeRepositoryMapper.class
        })
public interface SpareRepositoryMapper extends AuditableRepositoryMapper<SpareEntity, Spare> {}

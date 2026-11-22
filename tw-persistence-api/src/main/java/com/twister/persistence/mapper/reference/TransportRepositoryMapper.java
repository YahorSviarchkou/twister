package com.twister.persistence.mapper.reference;

import com.twister.domain.reference.Transport;
import com.twister.persistence.entity.reference.TransportEntity;
import com.twister.persistence.mapper.AuditableRepositoryMapper;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        uses = {CountryRepositoryMapper.class, TransportTypeRepositoryMapper.class, TransportBrandRepositoryMapper.class
        })
public interface TransportRepositoryMapper extends AuditableRepositoryMapper<TransportEntity, Transport> {}

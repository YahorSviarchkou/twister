package com.twister.persistence.mapper;

import com.twister.domain.AuditableModel;
import com.twister.persistence.entity.AuditableEntity;
import org.mapstruct.Mapping;

public interface AuditableRepositoryMapper<E extends AuditableEntity, D extends AuditableModel>
        extends RepositoryMapper<E, D> {

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Override
    E toEntity(D domain);
}

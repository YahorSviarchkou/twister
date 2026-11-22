package com.twister.persistence.mapper;

import java.util.List;

public interface RepositoryMapper<E, D> {

    // TODO(discuss): since Hibernate 6.6, saving an entity whose generated id has no row fails with an optimistic
    //  locking error instead of inserting a new row. Should DAO adapters report that as "not found"?
    E toEntity(D domain);

    D toDomain(E entity);

    List<E> toEntities(List<D> domains);

    List<D> toDomains(List<E> entities);
}

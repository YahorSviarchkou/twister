package com.twister.persistence.mapper;

import java.util.List;

public interface RepositoryMapper<E, D> {

    E toEntity(D domain);

    D toDomain(E entity);

    List<E> toEntities(List<D> domains);

    List<D> toDomains(List<E> entities);
}

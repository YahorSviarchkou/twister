package com.twister.persistence.repository.specification;

public record SpareFilter(
    String name,
    String model,
    String material,
    Long brandId,
    Long spareTypeId,
    Long transportTypeId,
    Integer issueYear,
    Long countryId
) {
}

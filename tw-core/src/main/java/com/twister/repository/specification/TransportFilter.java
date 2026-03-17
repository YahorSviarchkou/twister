package com.twister.repository.specification;

public record TransportFilter(
    String name,
    String model,
    Long brandId,
    Long transportTypeId,
    Integer issueYear,
    Long countryId
) {
}

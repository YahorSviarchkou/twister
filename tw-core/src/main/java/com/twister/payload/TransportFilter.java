package com.twister.payload;

public record TransportFilter(
    String name,
    String model,
    Long brandId,
    Long transportTypeId,
    Integer issueYear,
    Long countryId
) {
}

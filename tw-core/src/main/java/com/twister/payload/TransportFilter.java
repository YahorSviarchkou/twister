package com.twister.payload;

import lombok.Builder;

@Builder
public record TransportFilter(
        String name, String model, Long brandId, Long transportTypeId, Integer issueYear, Long countryId) {}

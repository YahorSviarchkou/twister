package com.twister.payload;

import lombok.Builder;

@Builder
public record SpareFilter(
        String name,
        String model,
        String material,
        Long brandId,
        Long spareTypeId,
        Long transportTypeId,
        Integer issueYear,
        Long countryId) {}

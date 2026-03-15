package com.twister.repository.specification;

public record RepairOrderFilter(
    String description,
    Long customerId,
    Long transportId
) {
}

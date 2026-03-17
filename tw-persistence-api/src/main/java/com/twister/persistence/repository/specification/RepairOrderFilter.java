package com.twister.persistence.repository.specification;

public record RepairOrderFilter(
    String description,
    Long customerId,
    Long transportId
) {
}

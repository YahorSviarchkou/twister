package com.twister.payload;

public record RepairOrderFilter(
    String description,
    Long customerId,
    Long transportId
) {
}

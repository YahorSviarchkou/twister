package com.twister.payload;

import lombok.Builder;

@Builder
public record RepairOrderFilter(String description, Long customerId, Long transportId) {}

package com.twister.payload;

import lombok.Builder;

@Builder
public record CustomerFilter(String surname, String name, String patronymic, String phone) {}

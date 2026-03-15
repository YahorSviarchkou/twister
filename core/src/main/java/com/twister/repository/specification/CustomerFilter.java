package com.twister.repository.specification;

public record CustomerFilter(
    String surname,
    String name,
    String patronymic,
    String phone
) {
}

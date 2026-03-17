package com.twister.persistence.repository.specification;

public record CustomerFilter(
    String surname,
    String name,
    String patronymic,
    String phone
) {
}

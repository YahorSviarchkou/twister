package com.twister.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TaskStatus {

    NEW("Новая"),
    IN_PROGRESS("В работе"),
    DONE("Завершена"),
    DEFERRED("Отложена");

    private final String name;
}

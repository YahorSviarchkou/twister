package com.twister.persistence.mapper;

import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.Test;

class RepositoryMapperWiringTest {

    @Test
    void wiresAllMappersWithoutCircularReferences() {
        assertThatCode(() -> MapperTestContext.start().close()).doesNotThrowAnyException();
    }
}

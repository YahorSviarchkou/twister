package com.twister.persistence.mapper;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

final class MapperTestContext {

    private MapperTestContext() {}

    static AnnotationConfigApplicationContext start() {
        var context = new AnnotationConfigApplicationContext();
        // A plain context tolerates circular references, Spring Boot does not.
        context.setAllowCircularReferences(false);
        context.scan(RepositoryMapper.class.getPackageName());
        context.refresh();
        return context;
    }
}

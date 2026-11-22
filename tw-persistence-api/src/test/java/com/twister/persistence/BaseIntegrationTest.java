package com.twister.persistence;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class BaseIntegrationTest {

    private static final String DB_DOCKER_IMAGE = "postgres:17-alpine";

    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DB_DOCKER_IMAGE);

    static {
        postgres.start();
    }

    @ServiceConnection
    static PostgreSQLContainer<?> postgresConnection = postgres;
}

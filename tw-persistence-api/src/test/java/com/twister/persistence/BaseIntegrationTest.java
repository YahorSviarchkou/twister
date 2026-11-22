package com.twister.persistence;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class BaseIntegrationTest {

    private static final String DB_DOCKER_IMAGE = "postgres:17-alpine";

    static final PostgreSQLContainer postgres = new PostgreSQLContainer(DB_DOCKER_IMAGE);

    static {
        postgres.start();
    }

    @ServiceConnection
    static PostgreSQLContainer postgresConnection = postgres;
}

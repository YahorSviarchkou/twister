package com.twister.persistence.migration;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** The production migrations on a real SQLite file, see tw-docs/adr/0009. */
class MigrationTest {

    @TempDir
    Path temporaryFolder;

    private String url;

    @BeforeEach
    void createDataFolder() throws IOException {
        // Like a Windows profile folder: a space and a letter outside ASCII.
        var dataFolder = Files.createDirectory(temporaryFolder.resolve("René Workshop"));
        // The settings of the desktop app: some statements, like a change of journal_mode, fail with them in place.
        url = "jdbc:sqlite:" + dataFolder.resolve("twister.db") + "?foreign_keys=on&journal_mode=WAL&busy_timeout=5000";
    }

    @Test
    void migratesAnEmptyFileFromTheBaseline() throws SQLException {
        flyway().migrate();

        assertThat(query("SELECT version FROM flyway_schema_history WHERE success = 1 ORDER BY installed_rank"))
                .startsWith("1");
        assertThat(query("SELECT name FROM pragma_table_list WHERE type = 'table'"))
                .contains("app_meta");
    }

    @Test
    void createsOnlyStrictTables() throws SQLException {
        flyway().migrate();

        var notStrict = query("SELECT name FROM pragma_table_list"
                + " WHERE type = 'table' AND strict = 0 AND name NOT LIKE 'sqlite_%'");

        // Flyway's own table is the only exception.
        assertThat(notStrict).containsExactly("flyway_schema_history");
    }

    @Test
    void keepsTheDataOfAnExistingFile() throws SQLException {
        flyway().migrate();
        try (var connection = DriverManager.getConnection(url);
                var statement = connection.createStatement()) {
            statement.executeUpdate("INSERT INTO app_meta (key, value) VALUES ('workshop', 'kept')");
        }

        var result = flyway().migrate();

        assertThat(result.migrationsExecuted).isZero();
        assertThat(query("SELECT value FROM app_meta")).containsExactly("kept");
    }

    private Flyway flyway() {
        return Flyway.configure().dataSource(url, null, null).load();
    }

    private List<String> query(String sql) throws SQLException {
        try (var connection = DriverManager.getConnection(url);
                var statement = connection.createStatement();
                var rows = statement.executeQuery(sql)) {
            var values = new ArrayList<String>();
            while (rows.next()) {
                values.add(rows.getString(1));
            }
            return values;
        }
    }
}

package com.atul.gitbook.learn;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.net.URI;
import java.util.Set;

@Testcontainers
@Configuration
public class TestContainerConfig {

    private static PostgreSQLContainer fSqlContainer;

    @Bean("data_source")
    DataSource configureTestDataSource(
            @Value("${database.url}") final String jdbcUrl,
            @Value("${database.username}") final String username,
            @Value("${database.password}") final String password,
            @Value("${test.container.postgres}") final String postgresContainer,
            @Value("${flyway.default.locations}") final Set<String> flywayLocations) {
        if (fSqlContainer == null) {
            fSqlContainer = new PostgreSQLContainer(postgresContainer);
            final var createScript = this.getClass().getClassLoader().getResource("test-db-creation.sql");
            if (createScript != null) {
                fSqlContainer.withInitScript("test-db-creation.sql");
            }
            fSqlContainer.start();
        }
        // substring(5) strips the "jdbc:" from the front of the URI, otherwise .create() fails.
        final var queryParams = URI.create(jdbcUrl.substring(5));
        final var url = fSqlContainer.getJdbcUrl() + "&" + queryParams.getRawQuery();
        final var dataSource = DataSourceBuilder.create()
                .url(url).username(username).password(password).build();
        configureFlyway(dataSource, flywayLocations);
        return dataSource;
    }

    private static void configureFlyway(
            final DataSource dataSource,
            final Set<String> flywayLocations) {
        Flyway.configure()
                .dataSource(dataSource)
                .baselineOnMigrate(true)
                .baselineVersion("0")
                .locations(flywayLocations.toArray(new String[]{}))
                .target(MigrationVersion.LATEST)
                .validateOnMigrate(true)
                .load()
                .migrate();
    }
}

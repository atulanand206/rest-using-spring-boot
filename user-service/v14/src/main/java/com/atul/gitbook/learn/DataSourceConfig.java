package com.atul.gitbook.learn;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Set;

@Configuration
public class DataSourceConfig {

    @Bean("data_source")
    DataSource configureDataSource(
            @Value("${database.url}") final String jdbcUrl,
            @Value("${database.username}") final String username,
            @Value("${database.password}") final String password,
            @Value("${flyway.default.locations}") final Set<String> flywayLocations) {
        final var dataSource = DataSourceBuilder.create()
                .url(jdbcUrl).username(username).password(password).build();
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

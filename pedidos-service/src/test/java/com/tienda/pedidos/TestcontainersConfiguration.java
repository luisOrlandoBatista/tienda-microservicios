package com.tienda.pedidos;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.postgresql.PostgreSQLContainer;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    @Bean
    PostgreSQLContainer postgresContainer() {
        return new PostgreSQLContainer("postgres:18");
    }

    @Bean
    DynamicPropertyRegistrar propiedadesBaseDeDatos(PostgreSQLContainer postgres) {
        return registry -> {
            // Conexión REACTIVA (R2DBC) que usa la aplicación
            registry.add("spring.r2dbc.url", () -> "r2dbc:postgresql://%s:%d/%s".formatted(
                    postgres.getHost(), postgres.getMappedPort(5432), postgres.getDatabaseName()));
            registry.add("spring.r2dbc.username", postgres::getUsername);
            registry.add("spring.r2dbc.password", postgres::getPassword);
            // Conexión JDBC que usa Flyway
            registry.add("spring.flyway.url", postgres::getJdbcUrl);
            registry.add("spring.flyway.user", postgres::getUsername);
            registry.add("spring.flyway.password", postgres::getPassword);
        };
    }
}
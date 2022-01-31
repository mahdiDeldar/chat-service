package com.chubock.chatservice.conf;

import org.junit.ClassRule;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

public class TestsContainerApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @ClassRule
    public static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:12.6")
            .withDatabaseName("shervin")
            .withUsername("shervin")
            .withPassword("shervin");
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {

        postgres.start();

        TestPropertyValues.of(
                "spring.cloud.discovery.enabled=false",
                "spring.datasource.url=" + postgres.getJdbcUrl(),
                "spring.datasource.username=" + postgres.getUsername(),
                "spring.datasource.password=" + postgres.getPassword()
        ).applyTo(applicationContext.getEnvironment());

    }
}

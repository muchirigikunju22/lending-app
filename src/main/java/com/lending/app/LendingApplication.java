package com.lending.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.URI;

@SpringBootApplication
@EnableScheduling
public class LendingApplication {

    public static void main(String[] args) {
        configureDatabaseFromRenderEnv();
        SpringApplication.run(LendingApplication.class, args);
    }

    private static void configureDatabaseFromRenderEnv() {
        String databaseUrl = System.getenv("DATABASE_URL");
        if (databaseUrl == null || !databaseUrl.startsWith("postgres://")) {
            return;
        }

        try {
            URI uri = URI.create(databaseUrl);
            String username = uri.getUserInfo() != null ? uri.getUserInfo().split(":")[0] : null;
            String password = uri.getUserInfo() != null && uri.getUserInfo().contains(":")
                    ? uri.getUserInfo().split(":")[1]
                    : null;
            String jdbcUrl = String.format("jdbc:postgresql://%s:%d%s",
                    uri.getHost(), uri.getPort(), uri.getPath());

            System.setProperty("spring.datasource.url", jdbcUrl);
            if (username != null) {
                System.setProperty("spring.datasource.username", username);
            }
            if (password != null) {
                System.setProperty("spring.datasource.password", password);
            }
        } catch (Exception e) {
            System.err.println("Failed to parse DATABASE_URL: " + e.getMessage());
        }
    }
}

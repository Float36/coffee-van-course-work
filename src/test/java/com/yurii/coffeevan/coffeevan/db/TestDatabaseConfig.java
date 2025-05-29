package com.yurii.coffeevan.coffeevan.db;

public class TestDatabaseConfig {
    private static final String TEST_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    private static final String TEST_USER = "sa";
    private static final String TEST_PASSWORD = "";

    private static String originalUrl;
    private static String originalUser;
    private static String originalPassword;

    public static void initializeTestConfig() {
        // Save original configuration
        originalUrl = DatabaseConfig.getUrl();
        originalUser = DatabaseConfig.getUser();
        originalPassword = DatabaseConfig.getPassword();

        // Set test configuration
        System.setProperty("db.url", TEST_URL);
        System.setProperty("db.user", TEST_USER);
        System.setProperty("db.password", TEST_PASSWORD);
    }

    public static void resetToDefaultConfig() {
        if (originalUrl != null) {
            System.setProperty("db.url", originalUrl);
        }
        if (originalUser != null) {
            System.setProperty("db.user", originalUser);
        }
        if (originalPassword != null) {
            System.setProperty("db.password", originalPassword);
        }
    }

    public static String getTestUrl() {
        return TEST_URL;
    }

    public static String getTestUser() {
        return TEST_USER;
    }

    public static String getTestPassword() {
        return TEST_PASSWORD;
    }
} 
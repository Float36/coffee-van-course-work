package com.yurii.coffeevan.coffeevan.db;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Database Config Tests")
class DatabaseConfigTest {

    @Test
    @DisplayName("Should provide database URL")
    void testGetUrl() {
        assertNotNull(DatabaseConfig.getUrl());
        assertTrue(DatabaseConfig.getUrl().startsWith("jdbc:"));
    }

    @Test
    @DisplayName("Should provide database user")
    void testGetUser() {
        assertNotNull(DatabaseConfig.getUser());
        assertFalse(DatabaseConfig.getUser().isEmpty());
    }

    @Test
    @DisplayName("Should provide database password")
    void testGetPassword() {
        assertNotNull(DatabaseConfig.getPassword());
        // Note: Password can be empty, but the method should not return null
    }

    @Test
    @DisplayName("Should use test configuration in test environment")
    void testTestConfiguration() {
        TestDatabaseConfig.initializeTestConfig();
        
        assertTrue(System.getProperty("db.url").contains("h2:mem:testdb"));
        assertEquals("sa", System.getProperty("db.user"));
        assertEquals("", System.getProperty("db.password"));
    }
} 
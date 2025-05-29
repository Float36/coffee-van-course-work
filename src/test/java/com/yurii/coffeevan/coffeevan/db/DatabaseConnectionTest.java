package com.yurii.coffeevan.coffeevan.db;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

@DisplayName("Database Connection Tests")
class DatabaseConnectionTest {

    @BeforeAll
    static void setUpTestDatabase() {
        TestDatabaseConfig.initializeTestConfig();
    }

    @Test
    @DisplayName("Should establish database connection")
    void testDatabaseConnection() {
        assertDoesNotThrow(() -> {
            try (Connection conn = DatabaseConnection.getConnection()) {
                assertTrue(conn.isValid(1));
                assertFalse(conn.isClosed());
            }
        });
    }

    @Test
    @DisplayName("Should create tables successfully")
    void testTableCreation() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Check if tables exist
            ResultSet vansTable = conn.getMetaData().getTables(null, null, "VANS", null);
            ResultSet coffeeTable = conn.getMetaData().getTables(null, null, "COFFEE", null);
            
            assertTrue(vansTable.next(), "Vans table should exist");
            assertTrue(coffeeTable.next(), "Coffee table should exist");
        } catch (SQLException e) {
            fail("Failed to check tables: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Should have correct table structure")
    void testTableStructure() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Check vans table columns
            ResultSet vansColumns = conn.getMetaData().getColumns(null, null, "VANS", null);
            assertTrue(vansColumns.next());
            assertEquals("id", vansColumns.getString("COLUMN_NAME").toLowerCase());
            assertTrue(vansColumns.next());
            assertEquals("total_volume", vansColumns.getString("COLUMN_NAME").toLowerCase());
            assertTrue(vansColumns.next());
            assertEquals("created_at", vansColumns.getString("COLUMN_NAME").toLowerCase());

            // Check coffee table columns
            ResultSet coffeeColumns = conn.getMetaData().getColumns(null, null, "COFFEE", null);
            assertTrue(coffeeColumns.next());
            assertEquals("id", coffeeColumns.getString("COLUMN_NAME").toLowerCase());
            assertTrue(coffeeColumns.next());
            assertEquals("van_id", coffeeColumns.getString("COLUMN_NAME").toLowerCase());
            assertTrue(coffeeColumns.next());
            assertEquals("name", coffeeColumns.getString("COLUMN_NAME").toLowerCase());
        } catch (SQLException e) {
            fail("Failed to check table structure: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Should maintain single connection instance")
    void testConnectionSingleton() throws SQLException {
        Connection conn1 = DatabaseConnection.getConnection();
        Connection conn2 = DatabaseConnection.getConnection();
        
        assertSame(conn1, conn2, "Should return the same connection instance");
    }

    @Test
    @DisplayName("Should close connection successfully")
    void testCloseConnection() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        DatabaseConnection.closeConnection();
        
        assertTrue(conn.isClosed(), "Connection should be closed");
    }

    @AfterEach
    void tearDown() {
        DatabaseConnection.closeConnection();
    }

    @AfterAll
    static void tearDownAll() {
        TestDatabaseConfig.resetToDefaultConfig();
    }
} 
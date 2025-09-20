package com.yurii.coffeevan.coffeevan.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

// Керування підключенням до бд
public class DatabaseConnection {
    private static Connection connection = null;

    // Створює підключення до MySQL
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                connection = DriverManager.getConnection(
                    DatabaseConfig.getUrl(),
                    DatabaseConfig.getUser(),
                    DatabaseConfig.getPassword()
                );
            } catch (SQLException e) {
                throw e;
            }
        }
        return connection;
    }

    // Створює таблиці якщо їх ще нема
    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS vans (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    total_volume INT NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS coffee (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    van_id INT,
                    name VARCHAR(255) NOT NULL,
                    type VARCHAR(50) NOT NULL,
                    volume INT NOT NULL,
                    price DOUBLE NOT NULL,
                    weight INT NOT NULL,
                    quality INT NOT NULL,
                    quantity INT NOT NULL,
                    FOREIGN KEY (van_id) REFERENCES vans(id)
                )
            """);

        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database: " + e.getMessage());
        }
    }

    // Закриває з’єднання з базою, якщо воно відкрите
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
} 

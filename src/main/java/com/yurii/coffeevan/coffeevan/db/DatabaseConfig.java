package com.yurii.coffeevan.coffeevan.db;

// Конфігураційний клас для підключення
public class DatabaseConfig {
    private static final String URL = "jdbc:mysql://localhost:3306/coffee_van_db";
    private static final String USER = "root";
    private static final String PASSWORD = "password";

    public static String getUrl() {
        return URL;
    }

    public static String getUser() {
        return USER;
    }

    public static String getPassword() {
        return PASSWORD;
    }
} 
package com.yurii.coffeevan.coffeevan.util;

import org.junit.jupiter.api.Test;

public class LoggerUtilTest {

    @Test
    public void testAllLoggingLevels() {
        // Test info level
        LoggerUtil.info("This is an INFO test message");

        // Test warning level
        LoggerUtil.warn("This is a WARNING test message");

        // Test debug level
        LoggerUtil.debug("This is a DEBUG test message");

        // Test error level (this will trigger email notification)
        LoggerUtil.error("This is an ERROR test message");

        // Test error with exception
        try {
            throw new RuntimeException("Test exception");
        } catch (Exception e) {
            LoggerUtil.error("This is an ERROR message with exception", e);
        }
    }
} 
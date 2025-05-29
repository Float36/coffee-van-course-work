package com.yurii.coffeevan.coffeevan.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Створюємо об'єкти для подальшого логування
public class LoggerUtil {
    private static final Logger logger = LogManager.getLogger("com.yurii.coffeevan.coffeevan");
    private static final Logger errorLogger = LogManager.getLogger("com.yurii.coffeevan.coffeevan.error");

    public static void info(String message) {
        logger.info(message);
    }

    public static void warn(String message) {
        logger.warn(message);
    }

    public static void error(String message) {
        logger.error(message);
        errorLogger.error(message); // Логуємо помилку також в errorLogger для відправки на пошту
    }

    public static void error(String message, Throwable throwable) {
        logger.error(message, throwable);
        errorLogger.error(message, throwable); // Логуємо помилку також в errorLogger для відправки на пошту
    }

    public static void debug(String message) {
        logger.debug(message);
    }
} 
package com.yurii.coffeevan.coffeevan.util;

import org.junit.jupiter.api.Test;

public class EmailLoggerTest {
    
    @Test
    public void testEmailNotification() {
        // Відправляємо тестове повідомлення про помилку
        LoggerUtil.error("TEST EMAIL: Перевірка налаштування відправки email через SSL");
        
        // Чекаємо трохи, щоб дати час на відправку email
        try {
            Thread.sleep(5000); // Чекаємо 5 секунд
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
} 
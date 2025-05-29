package com.yurii.coffeevan.coffeevan.controller;

import com.yurii.coffeevan.coffeevan.VanCoffeeController;
import com.yurii.coffeevan.coffeevan.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

@DisplayName("Van Coffee Controller Tests")
class VanCoffeeControllerTest {
    private ObservableList<Coffee> allCoffee;
    private FilteredList<Coffee> filteredCoffee;
    private SortedList<Coffee> sortedCoffee;

    @BeforeEach
    void setUp() {
        allCoffee = FXCollections.observableArrayList();
        filteredCoffee = new FilteredList<>(allCoffee, p -> true);
        sortedCoffee = new SortedList<>(filteredCoffee);

        // Додаємо тестові дані
        allCoffee.addAll(
            new BeanCoffee("Арабіка Преміум", 1000, 250.0, 500, 90, 2),
            new GroundCoffee("Колумбія Оригінал", 750, 180.0, 400, 75, 3),
            new InstantJarCoffee("Голд Класік", 500, 150.0, 300, 60, 4),
            new InstantPacketCoffee("Експрес Лайт", 250, 100.0, 200, 45, 5)
        );
    }

    @Test
    @DisplayName("Should filter coffee by type correctly")
    void testTypeFiltering() {
        // Фільтруємо за типом "Зернова"
        filteredCoffee.setPredicate(coffee -> coffee.getType().equals("Зернова"));
        assertEquals(1, filteredCoffee.size());
        assertEquals("Арабіка Преміум", filteredCoffee.get(0).getName());

        // Фільтруємо за типом "Розчинна (банка)"
        filteredCoffee.setPredicate(coffee -> coffee.getType().equals("Розчинна (банка)"));
        assertEquals(1, filteredCoffee.size());
        assertEquals("Голд Класік", filteredCoffee.get(0).getName());
    }

    @Test
    @DisplayName("Should filter coffee by quality type correctly")
    void testQualityTypeFiltering() {
        // Фільтруємо за преміум якістю (81-100)
        filteredCoffee.setPredicate(coffee -> coffee.getQuality() >= 81 && coffee.getQuality() <= 100);
        assertEquals(1, filteredCoffee.size(), "Має бути одна кава преміум якості");
        assertEquals("Арабіка Преміум", filteredCoffee.get(0).getName());

        // Фільтруємо за високою якістю (61-80)
        filteredCoffee.setPredicate(coffee -> coffee.getQuality() >= 61 && coffee.getQuality() <= 80);
        assertEquals(1, filteredCoffee.size(), "Має бути одна кава високої якості");
        assertEquals("Колумбія Оригінал", filteredCoffee.get(0).getName());

        // Фільтруємо за середньою якістю (41-60)
        filteredCoffee.setPredicate(coffee -> coffee.getQuality() >= 41 && coffee.getQuality() <= 60);
        assertEquals(2, filteredCoffee.size(), "Має бути дві кави середньої якості");
        assertTrue(filteredCoffee.stream().anyMatch(coffee -> coffee.getName().equals("Голд Класік")));
        assertTrue(filteredCoffee.stream().anyMatch(coffee -> coffee.getName().equals("Експрес Лайт")));

        // Фільтруємо за якістю нижче середньої (21-40)
        filteredCoffee.setPredicate(coffee -> coffee.getQuality() >= 21 && coffee.getQuality() <= 40);
        assertEquals(0, filteredCoffee.size(), "Не має бути кави з якістю нижче середньої");

        // Фільтруємо за низькою якістю (1-20)
        filteredCoffee.setPredicate(coffee -> coffee.getQuality() >= 1 && coffee.getQuality() <= 20);
        assertEquals(0, filteredCoffee.size(), "Не має бути кави низької якості");
    }

    @Test
    @DisplayName("Should calculate total statistics correctly")
    void testStatisticsCalculation() {
        // Перевіряємо загальний об'єм
        int totalVolume = allCoffee.stream()
            .mapToInt(coffee -> coffee.getVolume() * coffee.getQuantity())
            .sum();
        assertEquals(7500, totalVolume); // (1000*2 + 750*3 + 500*4 + 250*5)

        // Перевіряємо загальну вагу
        int totalWeight = allCoffee.stream()
            .mapToInt(coffee -> coffee.getWeight() * coffee.getQuantity())
            .sum();
        assertEquals(4400, totalWeight); // (500*2 + 400*3 + 300*4 + 200*5)

        // Перевіряємо середню якість
        double averageQuality = allCoffee.stream()
            .mapToInt(Coffee::getQuality)
            .average()
            .orElse(0.0);
        assertEquals(67.5, averageQuality); // (90 + 75 + 60 + 45) / 4

        // Перевіряємо загальну вартість
        double totalPrice = allCoffee.stream()
            .mapToDouble(coffee -> coffee.getPrice() * coffee.getQuantity())
            .sum();
        assertEquals(2140.0, totalPrice); // (250*2 + 180*3 + 150*4 + 100*5)
    }

    @Test
    @DisplayName("Should reset filters correctly")
    void testFilterReset() {
        // Спочатку застосовуємо фільтр
        filteredCoffee.setPredicate(coffee -> coffee.getType().equals("Зернова"));
        assertEquals(1, filteredCoffee.size());

        // Скидаємо фільтр
        filteredCoffee.setPredicate(coffee -> true);
        assertEquals(4, filteredCoffee.size());
    }

    @Test
    @DisplayName("Should handle empty coffee list")
    void testEmptyList() {
        allCoffee.clear();
        
        assertEquals(0, filteredCoffee.size());
        assertEquals(0, allCoffee.stream()
            .mapToInt(coffee -> coffee.getVolume() * coffee.getQuantity())
            .sum());
    }

    @Test
    @DisplayName("Should update filtered list when source list changes")
    void testFilteredListUpdate() {
        // Початковий розмір
        assertEquals(4, filteredCoffee.size());

        // Додаємо нову каву
        allCoffee.add(new BeanCoffee("Нова Арабіка", 1000, 300.0, 500, 95, 1));
        assertEquals(5, filteredCoffee.size());

        // Видаляємо каву
        allCoffee.remove(0);
        assertEquals(4, filteredCoffee.size());
    }

    @Test
    @DisplayName("Should handle multiple filters combination")
    void testMultipleFilters() {
        // Застосовуємо комбінований фільтр: тип "Зернова" та якість >= 85
        filteredCoffee.setPredicate(coffee -> 
            coffee.getType().equals("Зернова") && coffee.getQuality() >= 85
        );

        assertEquals(1, filteredCoffee.size());
        assertEquals("Арабіка Преміум", filteredCoffee.get(0).getName());
    }
} 
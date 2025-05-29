package com.yurii.coffeevan.coffeevan.controller;

import com.yurii.coffeevan.coffeevan.VanController;
import com.yurii.coffeevan.coffeevan.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

@DisplayName("Van Controller Tests")
class VanControllerTest {
    private Van van;
    private ObservableList<Coffee> allCoffee;
    private FilteredList<Coffee> filteredCoffee;

    @BeforeEach
    void setUp() {
        van = new Van();
        allCoffee = FXCollections.observableArrayList();
        filteredCoffee = new FilteredList<>(allCoffee, p -> true);

        // Додаємо тестові дані
        Coffee coffee1 = new BeanCoffee("Арабіка", 1000, 250.0, 500, 85, 2);
        Coffee coffee2 = new GroundCoffee("Колумбія", 750, 180.0, 400, 65, 3);
        Coffee coffee3 = new InstantJarCoffee("Голд", 500, 150.0, 300, 45, 4);
        Coffee coffee4 = new InstantPacketCoffee("Експрес", 250, 100.0, 200, 25, 5);

        van.addCoffee(coffee1);
        van.addCoffee(coffee2);
        van.addCoffee(coffee3);
        van.addCoffee(coffee4);

        allCoffee.setAll(van.getAllCoffee());
    }

    @Test
    @DisplayName("Should filter coffee by type correctly")
    void testFilterByType() {
        // Тестуємо фільтрацію за типом "Зернова"
        filteredCoffee.setPredicate(coffee -> coffee.getType().equals("Зернова"));
        assertEquals(1, filteredCoffee.size());
        assertEquals("Арабіка", filteredCoffee.get(0).getName());

        // Тестуємо фільтрацію за типом "Мелена"
        filteredCoffee.setPredicate(coffee -> coffee.getType().equals("Мелена"));
        assertEquals(1, filteredCoffee.size());
        assertEquals("Колумбія", filteredCoffee.get(0).getName());
    }

    @Test
    @DisplayName("Should filter coffee by quality type correctly")
    void testFilterByQualityType() {
        // Тестуємо фільтрацію за високою якістю (61-80)
        filteredCoffee.setPredicate(coffee -> coffee.getQuality() >= 61 && coffee.getQuality() <= 80);
        assertEquals(1, filteredCoffee.size());
        assertEquals("Колумбія", filteredCoffee.get(0).getName());

        // Тестуємо фільтрацію за преміум якістю (81-100)
        filteredCoffee.setPredicate(coffee -> coffee.getQuality() >= 81 && coffee.getQuality() <= 100);
        assertEquals(1, filteredCoffee.size());
        assertEquals("Арабіка", filteredCoffee.get(0).getName());
    }

    @Test
    @DisplayName("Should calculate total volume correctly")
    void testTotalVolumeCalculation() {
        int expectedTotalVolume = 
            (1000 * 2) + // Арабіка
            (750 * 3) +  // Колумбія
            (500 * 4) +  // Голд
            (250 * 5);   // Експрес

        assertEquals(expectedTotalVolume, van.getTotalVolume());
    }

    @Test
    @DisplayName("Should respect van capacity limits")
    void testVanCapacityLimits() {
        // Спроба додати каву, яка перевищить місткість фургона
        Coffee largeCoffee = new BeanCoffee("Велика партія", 1000000, 1000.0, 5000, 80, 6);
        assertFalse(van.addCoffee(largeCoffee));
        
        // Перевіряємо, що початкова кава все ще там
        assertEquals(4, van.getAllCoffee().size());
    }

    @ParameterizedTest
    @CsvSource({
        "Арабіка, Зернова, 1000, 250.0, 500, 85, 2, true",
        "Колумбія, Мелена, 750, 180.0, 400, 65, 3, true",
        ", Зернова, 1000, 250.0, 500, 85, 2, false",        // Порожнє ім'я
        "Тест, , 1000, 250.0, 500, 85, 2, false",           // Порожній тип
        "Тест, Зернова, -1000, 250.0, 500, 85, 2, false",   // Від'ємний об'єм
        "Тест, Зернова, 1000, -250.0, 500, 85, 2, false",   // Від'ємна ціна
        "Тест, Зернова, 1000, 250.0, -500, 85, 2, false",   // Від'ємна вага
        "Тест, Зернова, 1000, 250.0, 500, -85, 2, false",   // Від'ємна якість
        "Тест, Зернова, 1000, 250.0, 500, 85, -2, false"    // Від'ємна кількість
    })
    @DisplayName("Should validate coffee input correctly")
    void testCoffeeInputValidation(String name, String type, int volume, double price, 
                                 int weight, int quality, int quantity, boolean isValid) {
        if (isValid) {
            Coffee coffee = switch (type) {
                case "Зернова" -> new BeanCoffee(name, volume, price, weight, quality, quantity);
                case "Мелена" -> new GroundCoffee(name, volume, price, weight, quality, quantity);
                case "Розчинна (банка)" -> new InstantJarCoffee(name, volume, price, weight, quality, quantity);
                case "Розчинна (пакетик)" -> new InstantPacketCoffee(name, volume, price, weight, quality, quantity);
                default -> throw new IllegalArgumentException("Невідомий тип кави");
            };
            
            assertTrue(van.addCoffee(coffee));
        } else {
            assertThrows(IllegalArgumentException.class, () -> {
                Coffee coffee = new Coffee(name, type, volume, price, weight, quality, quantity);
                van.addCoffee(coffee);
            });
        }
    }

    @Test
    @DisplayName("Should sort coffee by price to weight ratio correctly")
    void testSortByPriceToWeight() {
        // Додаємо каву з різними співвідношеннями ціна/вага
        Coffee coffee1 = new BeanCoffee("Дорога", 1000, 1000.0, 500, 85, 1); // 2.0 грн/г
        Coffee coffee2 = new BeanCoffee("Середня", 1000, 500.0, 500, 85, 1);  // 1.0 грн/г
        Coffee coffee3 = new BeanCoffee("Дешева", 1000, 250.0, 500, 85, 1);   // 0.5 грн/г

        Van testVan = new Van();
        testVan.addCoffee(coffee1);
        testVan.addCoffee(coffee2);
        testVan.addCoffee(coffee3);

        var sortedCoffee = testVan.sortByPricePerWeight();
        
        assertEquals("Дешева", sortedCoffee.get(0).getName());
        assertEquals("Середня", sortedCoffee.get(1).getName());
        assertEquals("Дорога", sortedCoffee.get(2).getName());
    }

    @Test
    @DisplayName("Should handle coffee removal correctly")
    void testCoffeeRemoval() {
        Coffee coffeeToRemove = allCoffee.get(0);
        
        assertTrue(van.removeCoffee(coffeeToRemove));
        assertEquals(3, van.getAllCoffee().size());
        assertFalse(van.getAllCoffee().contains(coffeeToRemove));
    }

    @Test
    @DisplayName("Should clear van correctly")
    void testVanClear() {
        assertFalse(van.getAllCoffee().isEmpty());
        
        van.clear();
        
        assertTrue(van.getAllCoffee().isEmpty());
        assertEquals(0, van.getTotalVolume());
    }
} 
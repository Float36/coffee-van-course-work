package com.yurii.coffeevan.coffeevan.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Coffee Model Tests")
class CoffeeTest {

    @Test
    @DisplayName("Should create coffee with correct parameters")
    void testCoffeeCreation() {
        Coffee coffee = new Coffee("Arabica", "Зернова", 1000, 250.0, 500, 85, 2);
        
        assertEquals("Arabica", coffee.getName());
        assertEquals("Зернова", coffee.getType());
        assertEquals(1000, coffee.getVolume());
        assertEquals(250.0, coffee.getPrice());
        assertEquals(500, coffee.getWeight());
        assertEquals(85, coffee.getQuality());
        assertEquals(2, coffee.getQuantity());
    }

    @Test
    @DisplayName("Should calculate total coffee volume correctly")
    void testGetCoffeeVolume() {
        Coffee coffee = new Coffee("Arabica", "Зернова", 1000, 250.0, 500, 85, 2);
        assertEquals(2000, coffee.getCoffeeVolume(), "Total volume should be volume * quantity");
    }

    @Test
    @DisplayName("Should calculate price to weight ratio correctly")
    void testGetPriceToWeightRatio() {
        Coffee coffee = new Coffee("Arabica", "Зернова", 1000, 250.0, 500, 85, 2);
        assertEquals(0.5, coffee.getPriceToWeightRatio(), "Price to weight ratio should be price / weight");
    }


    @ParameterizedTest
    @CsvSource({
        "Arabica, Робуста",
        "Робуста, Арабіка",
        "Арабіка, Колумбія"
    })
    @DisplayName("Should update coffee name correctly")
    void testSetName(String initialName, String newName) {
        Coffee coffee = new Coffee(initialName, "Зернова", 1000, 250.0, 500, 85, 2);
        coffee.setName(newName);
        assertEquals(newName, coffee.getName());
    }

    @ParameterizedTest
    @CsvSource({
        "1000, 2000",
        "500, 750",
        "2000, 1500"
    })
    @DisplayName("Should update volume correctly")
    void testSetVolume(int initialVolume, int newVolume) {
        Coffee coffee = new Coffee("Arabica", "Зернова", initialVolume, 250.0, 500, 85, 2);
        coffee.setVolume(newVolume);
        assertEquals(newVolume, coffee.getVolume());
    }

    @ParameterizedTest
    @CsvSource({
        "250.0, 300.0",
        "180.5, 200.0",
        "500.0, 450.0"
    })
    @DisplayName("Should update price correctly")
    void testSetPrice(double initialPrice, double newPrice) {
        Coffee coffee = new Coffee("Arabica", "Зернова", 1000, initialPrice, 500, 85, 2);
        coffee.setPrice(newPrice);
        assertEquals(newPrice, coffee.getPrice());
    }

    @ParameterizedTest
    @CsvSource({
        "500, 600",
        "250, 300",
        "1000, 800"
    })
    @DisplayName("Should update weight correctly")
    void testSetWeight(int initialWeight, int newWeight) {
        Coffee coffee = new Coffee("Arabica", "Зернова", 1000, 250.0, initialWeight, 85, 2);
        coffee.setWeight(newWeight);
        assertEquals(newWeight, coffee.getWeight());
    }

    @ParameterizedTest
    @CsvSource({
        "85, 90",
        "70, 75",
        "95, 100"
    })
    @DisplayName("Should update quality correctly")
    void testSetQuality(int initialQuality, int newQuality) {
        Coffee coffee = new Coffee("Arabica", "Зернова", 1000, 250.0, 500, initialQuality, 2);
        coffee.setQuality(newQuality);
        assertEquals(newQuality, coffee.getQuality());
    }
} 
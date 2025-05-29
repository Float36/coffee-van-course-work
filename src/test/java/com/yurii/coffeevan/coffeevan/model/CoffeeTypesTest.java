package com.yurii.coffeevan.coffeevan.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Coffee Types Tests")
class CoffeeTypesTest {

    @Test
    @DisplayName("Should create BeanCoffee with correct type")
    void testBeanCoffee() {
        Coffee coffee = new BeanCoffee("Premium Arabica", 1000, 250.0, 500, 90, 2);
        
        assertEquals("Premium Arabica", coffee.getName());
        assertEquals("Зернова", coffee.getType());
        assertEquals(1000, coffee.getVolume());
        assertEquals(250.0, coffee.getPrice());
        assertEquals(500, coffee.getWeight());
        assertEquals(90, coffee.getQuality());
        assertEquals(2, coffee.getQuantity());
    }

    @Test
    @DisplayName("Should create GroundCoffee with correct type")
    void testGroundCoffee() {
        Coffee coffee = new GroundCoffee("Ground Colombia", 750, 180.0, 400, 85, 3);
        
        assertEquals("Ground Colombia", coffee.getName());
        assertEquals("Мелена", coffee.getType());
        assertEquals(750, coffee.getVolume());
        assertEquals(180.0, coffee.getPrice());
        assertEquals(400, coffee.getWeight());
        assertEquals(85, coffee.getQuality());
        assertEquals(3, coffee.getQuantity());
    }

    @Test
    @DisplayName("Should create InstantJarCoffee with correct type")
    void testInstantJarCoffee() {
        Coffee coffee = new InstantJarCoffee("Gold Instant", 500, 150.0, 300, 75, 4);
        
        assertEquals("Gold Instant", coffee.getName());
        assertEquals("Розчинна (банка)", coffee.getType());
        assertEquals(500, coffee.getVolume());
        assertEquals(150.0, coffee.getPrice());
        assertEquals(300, coffee.getWeight());
        assertEquals(75, coffee.getQuality());
        assertEquals(4, coffee.getQuantity());
    }

    @Test
    @DisplayName("Should create InstantPacketCoffee with correct type")
    void testInstantPacketCoffee() {
        Coffee coffee = new InstantPacketCoffee("Express Pack", 250, 100.0, 200, 70, 5);
        
        assertEquals("Express Pack", coffee.getName());
        assertEquals("Розчинна (пакетик)", coffee.getType());
        assertEquals(250, coffee.getVolume());
        assertEquals(100.0, coffee.getPrice());
        assertEquals(200, coffee.getWeight());
        assertEquals(70, coffee.getQuality());
        assertEquals(5, coffee.getQuantity());
    }

    @Test
    @DisplayName("Should calculate coffee volume correctly for all types")
    void testCoffeeVolumeCalculation() {
        Coffee beanCoffee = new BeanCoffee("Bean", 1000, 250.0, 500, 90, 2);
        Coffee groundCoffee = new GroundCoffee("Ground", 750, 180.0, 400, 85, 3);
        Coffee jarCoffee = new InstantJarCoffee("Jar", 500, 150.0, 300, 75, 4);
        Coffee packetCoffee = new InstantPacketCoffee("Packet", 250, 100.0, 200, 70, 5);

        assertEquals(2000, beanCoffee.getCoffeeVolume());
        assertEquals(2250, groundCoffee.getCoffeeVolume());
        assertEquals(2000, jarCoffee.getCoffeeVolume());
        assertEquals(1250, packetCoffee.getCoffeeVolume());
    }

    @Test
    @DisplayName("Should calculate price to weight ratio correctly for all types")
    void testPriceToWeightRatio() {
        Coffee beanCoffee = new BeanCoffee("Bean", 1000, 250.0, 500, 90, 2);
        Coffee groundCoffee = new GroundCoffee("Ground", 750, 180.0, 400, 85, 3);
        Coffee jarCoffee = new InstantJarCoffee("Jar", 500, 150.0, 300, 75, 4);
        Coffee packetCoffee = new InstantPacketCoffee("Packet", 250, 100.0, 200, 70, 5);

        assertEquals(0.5, beanCoffee.getPriceToWeightRatio());
        assertEquals(0.45, groundCoffee.getPriceToWeightRatio());
        assertEquals(0.5, jarCoffee.getPriceToWeightRatio());
        assertEquals(0.5, packetCoffee.getPriceToWeightRatio());
    }
} 
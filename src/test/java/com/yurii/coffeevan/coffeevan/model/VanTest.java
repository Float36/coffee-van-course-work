package com.yurii.coffeevan.coffeevan.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

@DisplayName("Van Model Tests")
class VanTest {
    private Van van;
    private Coffee coffee1;
    private Coffee coffee2;
    private Coffee coffee3;

    @BeforeEach
    void setUp() {
        van = new Van();
        coffee1 = new BeanCoffee("Arabica Premium", 1000, 250.0, 500, 90, 2);
        coffee2 = new GroundCoffee("Colombia Ground", 750, 180.0, 400, 85, 3);
        coffee3 = new InstantJarCoffee("Gold Instant", 500, 150.0, 300, 75, 4);
    }

    @Test
    @DisplayName("Should start with empty coffee list and zero volume")
    void testInitialState() {
        assertTrue(van.getAllCoffee().isEmpty());
        assertEquals(0.0, van.getCurrentVolume());
    }

    @Test
    @DisplayName("Should add coffee successfully when capacity not exceeded")
    void testAddCoffeeSuccess() {
        assertTrue(van.addCoffee(coffee1));
        assertEquals(1, van.getAllCoffee().size());
        assertEquals(2000.0, van.getCurrentVolume()); // 1000 * 2 (volume * quantity)
    }

    @Test
    @DisplayName("Should fail to add coffee when capacity would be exceeded")
    void testAddCoffeeCapacityExceeded() {
        // Create coffee that would exceed capacity
        Coffee largeCoffee = new BeanCoffee("Large Batch", 1000000, 1000.0, 5000, 80, 6);
        assertFalse(van.addCoffee(largeCoffee));
        assertTrue(van.getAllCoffee().isEmpty());
    }

    @Test
    @DisplayName("Should remove coffee successfully")
    void testRemoveCoffee() {
        van.addCoffee(coffee1);
        van.addCoffee(coffee2);
        
        assertTrue(van.removeCoffee(coffee1));
        assertEquals(1, van.getAllCoffee().size());
        assertEquals(2250.0, van.getCurrentVolume()); // 750 * 3 (remaining coffee2)
    }

    @Test
    @DisplayName("Should return false when removing non-existent coffee")
    void testRemoveNonExistentCoffee() {
        van.addCoffee(coffee1);
        assertFalse(van.removeCoffee(coffee3));
        assertEquals(1, van.getAllCoffee().size());
    }

    @Test
    @DisplayName("Should clear all coffee from van")
    void testClear() {
        van.addCoffee(coffee1);
        van.addCoffee(coffee2);
        van.clear();
        
        assertTrue(van.getAllCoffee().isEmpty());
        assertEquals(0.0, van.getCurrentVolume());
    }

    @Test
    @DisplayName("Should calculate total volume correctly")
    void testGetTotalVolume() {
        van.addCoffee(coffee1);
        van.addCoffee(coffee2);
        
        int expectedVolume = coffee1.getCoffeeVolume() + coffee2.getCoffeeVolume();
        assertEquals(expectedVolume, van.getTotalVolume());
    }

    @Test
    @DisplayName("Should filter coffee by type correctly")
    void testFilterByType() {
        van.addCoffee(coffee1);
        van.addCoffee(coffee2);
        van.addCoffee(coffee3);

        List<Coffee> beanCoffee = van.filterByType("Зернова");
        assertEquals(1, beanCoffee.size());
        assertEquals("Arabica Premium", beanCoffee.get(0).getName());

        List<Coffee> groundCoffee = van.filterByType("Мелена");
        assertEquals(1, groundCoffee.size());
        assertEquals("Colombia Ground", groundCoffee.get(0).getName());
    }

    @Test
    @DisplayName("Should filter coffee by quality correctly")
    void testFilterByQuality() {
        van.addCoffee(coffee1);
        van.addCoffee(coffee2);
        van.addCoffee(coffee3);

        List<Coffee> highQualityCoffee = van.filterByQuality(85);
        assertEquals(2, highQualityCoffee.size());
    }

    @Test
    @DisplayName("Should sort coffee by price per weight correctly")
    void testSortByPricePerWeight() {
        van.addCoffee(coffee1);
        van.addCoffee(coffee2);
        van.addCoffee(coffee3);

        List<Coffee> sortedCoffee = van.sortByPricePerWeight();
        assertEquals(3, sortedCoffee.size());
        assertTrue(sortedCoffee.get(0).getPriceToWeightRatio() <= sortedCoffee.get(1).getPriceToWeightRatio());
        assertTrue(sortedCoffee.get(1).getPriceToWeightRatio() <= sortedCoffee.get(2).getPriceToWeightRatio());
    }

    @Test
    @DisplayName("Should sort coffee by quality correctly")
    void testSortByQuality() {
        van.addCoffee(coffee1);
        van.addCoffee(coffee2);
        van.addCoffee(coffee3);

        List<Coffee> sortedCoffee = van.sortByQuality();
        assertEquals(3, sortedCoffee.size());
        assertEquals(90, sortedCoffee.get(0).getQuality());
        assertEquals(85, sortedCoffee.get(1).getQuality());
        assertEquals(75, sortedCoffee.get(2).getQuality());
    }

    @Test
    @DisplayName("Should respect maximum capacity constant")
    void testMaxCapacity() {
        assertEquals(5000000.0, van.getMaxCapacity());
    }

    @Test
    @DisplayName("Should maintain safe copy of coffee list")
    void testGetAllCoffeeSafeCopy() {
        van.addCoffee(coffee1);
        List<Coffee> coffeeList = van.getAllCoffee();
        coffeeList.clear(); // Should not affect the van's internal list
        
        assertEquals(1, van.getAllCoffee().size());
    }
} 
package com.yurii.coffeevan.coffeevan.controller;

import com.yurii.coffeevan.coffeevan.VansController;
import com.yurii.coffeevan.coffeevan.model.*;
import com.yurii.coffeevan.coffeevan.db.VanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

@DisplayName("Vans Controller Tests")
class VansControllerTest {
    private VanService vanService;
    private ObservableList<VanService.VanEntry> vans;

    @BeforeEach
    void setUp() {
        vanService = mock(VanService.class);
        vans = FXCollections.observableArrayList();

        // Створюємо тестові дані
        List<VanService.VanEntry> testVans = Arrays.asList(
            new VanService.VanEntry(1, 5000, new Timestamp(System.currentTimeMillis())),
            new VanService.VanEntry(2, 3000, new Timestamp(System.currentTimeMillis())),
            new VanService.VanEntry(3, 7000, new Timestamp(System.currentTimeMillis()))
        );

        when(vanService.loadAllVans()).thenReturn(testVans);
        vans.addAll(testVans);
    }

    @Test
    @DisplayName("Should load vans correctly")
    void testLoadVans() {
        List<VanService.VanEntry> loadedVans = vanService.loadAllVans();
        
        assertEquals(3, loadedVans.size());
        assertEquals(5000, loadedVans.get(0).getTotalVolume());
        assertEquals(3000, loadedVans.get(1).getTotalVolume());
        assertEquals(7000, loadedVans.get(2).getTotalVolume());
    }

    @Test
    @DisplayName("Should delete van correctly")
    void testDeleteVan() {
        int vanIdToDelete = 1;
        when(vanService.deleteVan(vanIdToDelete)).thenReturn(true);

        assertTrue(vanService.deleteVan(vanIdToDelete));
        verify(vanService).deleteVan(vanIdToDelete);
    }

    @Test
    @DisplayName("Should handle van loading failure")
    void testVanLoadingFailure() {
        when(vanService.loadVan(anyInt())).thenReturn(null);

        Van loadedVan = vanService.loadVan(999);
        assertNull(loadedVan);
    }

    @Test
    @DisplayName("Should load van contents correctly")
    void testLoadVanContents() {
        int vanId = 1;
        Van testVan = new Van();
        testVan.addCoffee(new BeanCoffee("Тестова кава", 1000, 250.0, 500, 85, 2));

        when(vanService.loadVan(vanId)).thenReturn(testVan);

        Van loadedVan = vanService.loadVan(vanId);
        assertNotNull(loadedVan);
        assertEquals(1, loadedVan.getAllCoffee().size());
        assertEquals(2000, loadedVan.getTotalVolume()); // 1000 * 2
    }

    @Test
    @DisplayName("Should handle empty vans list")
    void testEmptyVansList() {
        when(vanService.loadAllVans()).thenReturn(List.of());

        List<VanService.VanEntry> loadedVans = vanService.loadAllVans();
        assertTrue(loadedVans.isEmpty());
    }

    @Test
    @DisplayName("Should update vans list after deletion")
    void testVansListUpdate() {
        // Видаляємо фургон
        vans.remove(0);
        assertEquals(2, vans.size());
        assertEquals(3000, vans.get(0).getTotalVolume());
        assertEquals(7000, vans.get(1).getTotalVolume());
    }

    @Test
    @DisplayName("Should handle van deletion failure")
    void testVanDeletionFailure() {
        int nonExistentVanId = 999;
        when(vanService.deleteVan(nonExistentVanId)).thenReturn(false);

        assertFalse(vanService.deleteVan(nonExistentVanId));
        verify(vanService).deleteVan(nonExistentVanId);
    }
} 
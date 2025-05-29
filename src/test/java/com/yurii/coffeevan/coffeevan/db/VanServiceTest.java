package com.yurii.coffeevan.coffeevan.db;

import com.yurii.coffeevan.coffeevan.model.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@DisplayName("Van Service Tests")
class VanServiceTest {
    private VanService vanService;
    private Van testVan;
    private List<Integer> testVanIds;

    @BeforeAll
    static void setUpTestDatabase() {
        TestDatabaseConfig.initializeTestConfig();
    }

    @BeforeEach
    void setUp() throws SQLException {
        vanService = new VanService(DatabaseConnection.getConnection());
        testVan = new Van();
        testVanIds = new ArrayList<>();
        
        // Add some test coffee to the van
        testVan.addCoffee(new BeanCoffee("Test Arabica", 1000, 250.0, 500, 90, 2));
        testVan.addCoffee(new GroundCoffee("Test Ground", 750, 180.0, 400, 85, 3));
        testVan.addCoffee(new InstantJarCoffee("Test Instant", 500, 150.0, 300, 75, 4));
    }

    @Test
    @DisplayName("Should save van with coffee successfully")
    void testSaveVan() {
        int vanId = vanService.saveVan(testVan);
        testVanIds.add(vanId);
        
        assertTrue(vanId > 0, "Van ID should be positive");

        Van loadedVan = vanService.loadVan(vanId);
        assertNotNull(loadedVan);
        assertEquals(testVan.getTotalVolume(), loadedVan.getTotalVolume());
        assertEquals(testVan.getAllCoffee().size(), loadedVan.getAllCoffee().size());
    }

    @Test
    @DisplayName("Should load all vans successfully")
    void testLoadAllVans() {
        // Save multiple vans
        int vanId1 = vanService.saveVan(testVan);
        int vanId2 = vanService.saveVan(testVan);
        testVanIds.add(vanId1);
        testVanIds.add(vanId2);
        
        List<VanService.VanEntry> vans = vanService.loadAllVans();
        assertNotNull(vans);
        assertTrue(vans.size() >= 2); // Може бути більше, якщо є інші фургони в базі
        
        // Перевіряємо, що наші тестові фургони є в списку
        boolean foundVan1 = false;
        boolean foundVan2 = false;
        for (VanService.VanEntry van : vans) {
            if (van.getId() == vanId1) foundVan1 = true;
            if (van.getId() == vanId2) foundVan2 = true;
        }
        assertTrue(foundVan1 && foundVan2, "Both test vans should be in the list");
    }

    @Test
    @DisplayName("Should load van with correct coffee data")
    void testLoadVanWithCoffee() {
        int vanId = vanService.saveVan(testVan);
        testVanIds.add(vanId);
        
        Van loadedVan = vanService.loadVan(vanId);

        List<Coffee> originalCoffee = testVan.getAllCoffee();
        List<Coffee> loadedCoffee = loadedVan.getAllCoffee();

        assertEquals(originalCoffee.size(), loadedCoffee.size());

        for (int i = 0; i < originalCoffee.size(); i++) {
            Coffee original = originalCoffee.get(i);
            Coffee loaded = loadedCoffee.get(i);

            assertEquals(original.getName(), loaded.getName());
            assertEquals(original.getType(), loaded.getType());
            assertEquals(original.getVolume(), loaded.getVolume());
            assertEquals(original.getPrice(), loaded.getPrice());
            assertEquals(original.getWeight(), loaded.getWeight());
            assertEquals(original.getQuality(), loaded.getQuality());
            assertEquals(original.getQuantity(), loaded.getQuantity());
        }
    }

    @Test
    @DisplayName("Should handle non-existent van ID")
    void testLoadNonExistentVan() {
        Van loadedVan = vanService.loadVan(999999); // Використовуємо дуже великий ID, щоб не зачепити існуючі
        assertNull(loadedVan);
    }

    @Test
    @DisplayName("Should delete van successfully")
    void testDeleteVan() {
        int vanId = vanService.saveVan(testVan);
        // Не додаємо до testVanIds, бо видалимо в рамках тесту
        
        boolean deleted = vanService.deleteVan(vanId);
        assertTrue(deleted, "Van should be deleted successfully");
        
        Van loadedVan = vanService.loadVan(vanId);
        assertNull(loadedVan);
    }

    @Test
    @DisplayName("Should handle deleting non-existent van")
    void testDeleteNonExistentVan() {
        boolean deleted = vanService.deleteVan(999999); // Використовуємо дуже великий ID, щоб не зачепити існуючі
        assertFalse(deleted, "Deleting non-existent van should return false");
    }

    @AfterEach
    void tearDown() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Видаляємо тільки ті фургони, які ми створили під час тесту
            try (Statement stmt = conn.createStatement()) {
                for (Integer vanId : testVanIds) {
                    // Спочатку видаляємо пов'язану каву
                    stmt.execute("DELETE FROM coffee WHERE van_id = " + vanId);
                    // Потім видаляємо фургон
                    stmt.execute("DELETE FROM vans WHERE id = " + vanId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        DatabaseConnection.closeConnection();
    }

    @AfterAll
    static void tearDownAll() {
        TestDatabaseConfig.resetToDefaultConfig();
    }
} 
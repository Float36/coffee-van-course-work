package com.yurii.coffeevan.coffeevan.db;

import com.yurii.coffeevan.coffeevan.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Це сервіс для взаємодії між бд та таблицями
public class VanService {
    
    private final Connection connection;

    public VanService(Connection connection) {
        this.connection = connection;
    }
    // Клас який представляє коротку інформацію про фургон
    public static class VanEntry {
        private final int id;
        private final int totalVolume;
        private final Timestamp createdAt;
        
        public VanEntry(int id, int totalVolume, Timestamp createdAt) {
            this.id = id;
            this.totalVolume = totalVolume;
            this.createdAt = createdAt;
        }
        
        public int getId() { return id; }
        public int getTotalVolume() { return totalVolume; }
        public Timestamp getCreatedAt() { return createdAt; }
    }

    // Зберігає фургон у таблицю vans і каву з нього в таблицю coffee
    public int saveVan(Van van) {
        int vanId = -1;
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Вставити фургон
                try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO vans (total_volume) VALUES (?)",
                    Statement.RETURN_GENERATED_KEYS
                )) {
                    stmt.setInt(1, van.getTotalVolume());
                    stmt.executeUpdate();

                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            vanId = rs.getInt(1);
                        }
                    }
                }

                // Вставити каву
                try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO coffee (van_id, name, type, volume, price, weight, quality, quantity) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
                )) {
                    for (Coffee coffee : van.getAllCoffee()) {
                        stmt.setInt(1, vanId);
                        stmt.setString(2, coffee.getName());
                        stmt.setString(3, coffee.getType());
                        stmt.setInt(4, coffee.getVolume());
                        stmt.setDouble(5, coffee.getPrice());
                        stmt.setInt(6, coffee.getWeight());
                        stmt.setInt(7, coffee.getQuality());
                        stmt.setInt(8, coffee.getQuantity());
                        stmt.executeUpdate();
                    }
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            return -1;
        }
        return vanId;
    }

    // Створює об'єкт, додає каву, повертає його
    public Van loadVan(int vanId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Перевіряємо чи існує id
            try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM vans WHERE id = ?")) {
                stmt.setInt(1, vanId);
                ResultSet rs = stmt.executeQuery();
                if (!rs.next()) {
                    return null;
                }
            }

            // Завантажуємо каву у фургон
            try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM coffee WHERE van_id = ?")) {
                stmt.setInt(1, vanId);
                ResultSet rs = stmt.executeQuery();

                Van van = new Van();
                while (rs.next()) {
                    Coffee coffee = createCoffeeFromResultSet(rs);
                    van.addCoffee(coffee);
                }
                return van;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    // Повертає список всіх фургонів у базі
    public List<VanEntry> loadAllVans() {
        List<VanEntry> vans = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM vans ORDER BY created_at DESC");
            while (rs.next()) {
                vans.add(new VanEntry(
                    rs.getInt("id"),
                    rs.getInt("total_volume"),
                    rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vans;
    }

    // Видаляє всі дані про фургон
    public boolean deleteVan(int vanId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Спочатку видаляє всю каву у фургоні
                try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM coffee WHERE van_id = ?")) {
                    stmt.setInt(1, vanId);
                    stmt.executeUpdate();
                }

                // Потім видаляє сам фургон
                try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM vans WHERE id = ?")) {
                    stmt.setInt(1, vanId);
                    int rowsAffected = stmt.executeUpdate();
                    conn.commit();
                    return rowsAffected > 0;
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    // Витягує поля кави з ResultSet і створює відповідний підклас Coffee залежно від type
    private Coffee createCoffeeFromResultSet(ResultSet rs) throws SQLException {
        String name = rs.getString("name");
        String type = rs.getString("type");
        int volume = rs.getInt("volume");
        double price = rs.getDouble("price");
        int weight = rs.getInt("weight");
        int quality = rs.getInt("quality");
        int quantity = rs.getInt("quantity");

        return switch (type) {
            case "Зернова" -> new BeanCoffee(name, volume, price, weight, quality, quantity);
            case "Мелена" -> new GroundCoffee(name, volume, price, weight, quality, quantity);
            case "Розчинна (банка)" -> new InstantJarCoffee(name, volume, price, weight, quality, quantity);
            case "Розчинна (пакетик)" -> new InstantPacketCoffee(name, volume, price, weight, quality, quantity);
            default -> new Coffee(name, type, volume, price, weight, quality, quantity);
        };
    }
} 
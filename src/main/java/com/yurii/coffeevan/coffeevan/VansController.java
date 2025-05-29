package com.yurii.coffeevan.coffeevan;

import com.yurii.coffeevan.coffeevan.model.Van;
import com.yurii.coffeevan.coffeevan.db.VanService;
import com.yurii.coffeevan.coffeevan.db.DatabaseConnection;
import com.yurii.coffeevan.coffeevan.util.LoggerUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


// Керування сторінкою вибору фургонів
public class VansController {
    
    @FXML
    private TableView<VanEntry> vansTable;
    
    @FXML
    private TableColumn<VanEntry, Integer> idColumn;
    
    @FXML
    private TableColumn<VanEntry, Integer> volumeColumn;
    
    @FXML
    private TableColumn<VanEntry, Timestamp> dateColumn;
    
    @FXML
    private Button showCoffeeButton;
    
    @FXML
    private Button closeButton;

    @FXML
    private Button deleteButton;
    
    private ObservableList<VanEntry> vans = FXCollections.observableArrayList();        // Список всіх фургонів
    private VanService vanService;
    
    @FXML
    public void initialize() {
        try {
            vanService = new VanService(DatabaseConnection.getConnection());        // Підключення до бд

            // Прив'язка даних до колонк
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            volumeColumn.setCellValueFactory(new PropertyValueFactory<>("totalVolume"));
            dateColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
            
            // Форматуванян дати
            dateColumn.setCellFactory(column -> new TableCell<>() {
                @Override
                protected void updateItem(Timestamp item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.toString().replace(".0", ""));
                    }
                }
            });

            // Прив'язка даних до таблиці
            vansTable.setItems(vans);

            // Додаємо слухач вибору рядка для активації/деактивації кнопок
            vansTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    boolean hasSelection = newSelection != null;
                    deleteButton.setDisable(!hasSelection);
                    showCoffeeButton.setDisable(!hasSelection);
                }
            );


            loadVans();
            LoggerUtil.info("Vans view initialized successfully");
        } catch (SQLException e) {
            LoggerUtil.error("Failed to initialize vans view", e);
            showAlert(Alert.AlertType.ERROR, "Помилка", "Не вдалося підключитися до бази даних: " + e.getMessage());
        }
    }

    // Метод для видалення обраного фургона зі списку
    @FXML
    private void handleDeleteVan() {
        VanEntry selectedVan = vansTable.getSelectionModel().getSelectedItem();
        if (selectedVan != null) {
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Підтвердження видалення");
            confirmDialog.setHeaderText(null);
            confirmDialog.setContentText("Ви впевнені, що хочете видалити фургон #" + selectedVan.getId() + 
                                      " та всю його каву? Цю дію неможливо відмінити.");

            Optional<ButtonType> result = confirmDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    // Видаляємо фургон з бази даних
                    vanService.deleteVan(selectedVan.getId());
                    // Видаляємо фургон з таблиці
                    vans.remove(selectedVan);
                    LoggerUtil.info("Deleted van #" + selectedVan.getId());
                    showAlert(Alert.AlertType.INFORMATION, "Видалено", 
                            "Фургон #" + selectedVan.getId() + " успішно видалено!");
                } catch (Exception e) {
                    LoggerUtil.error("Failed to delete van #" + selectedVan.getId(), e);
                    showAlert(Alert.AlertType.ERROR, "Помилка", 
                            "Не вдалося видалити фургон: " + e.getMessage());
                }
            }
        }
    }

    // Завантажуємо всі фургони з бд
    private void loadVans() {
        try {
            List<VanService.VanEntry> serviceEntries = vanService.loadAllVans();
            List<VanEntry> controllerEntries = serviceEntries.stream()
                .map(e -> new VanEntry(e.getId(), e.getTotalVolume(), e.getCreatedAt()))
                .collect(Collectors.toList());
            vans.setAll(controllerEntries);
            LoggerUtil.info("Loaded " + controllerEntries.size() + " vans from database");
        } catch (Exception e) {
            LoggerUtil.error("Failed to load vans data", e);
            showAlert(Alert.AlertType.ERROR, "Помилка", "Не вдалося завантажити дані фургонів: " + e.getMessage());
        }
    }

    // Показує вміст фургону відкриваючи нове вікно
    @FXML
    private void handleShowCoffee() {
        VanEntry selectedVan = vansTable.getSelectionModel().getSelectedItem();
        if (selectedVan == null) {
            LoggerUtil.warn("Attempt to show coffee contents without selecting a van");
            showAlert(Alert.AlertType.WARNING, "Попередження", "Будь ласка, виберіть фургон зі списку");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("van-coffee-view.fxml"));
            Parent root = loader.load();

            VanCoffeeController controller = loader.getController();
            controller.setVanInfo(selectedVan);
            controller.setCoffeeList(FXCollections.observableArrayList(vanService.loadVan(selectedVan.getId()).getAllCoffee()));

            Stage stage = new Stage();
            stage.setTitle("Вміст фургону #" + selectedVan.getId());
            stage.setScene(new Scene(root));
            stage.show();
            LoggerUtil.info("Opened coffee contents window for van #" + selectedVan.getId());
        } catch (Exception e) {
            LoggerUtil.error("Failed to open coffee contents for van #" + selectedVan.getId(), e);
            showAlert(Alert.AlertType.ERROR, "Помилка", "Не вдалося відкрити вікно фургонів: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleClose() {
        ((Stage) closeButton.getScene().getWindow()).close();
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // Внтурішній клас для представлення одного фургона у таблиці
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
} 
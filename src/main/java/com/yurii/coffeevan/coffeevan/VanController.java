package com.yurii.coffeevan.coffeevan;

import com.yurii.coffeevan.coffeevan.model.*;
import com.yurii.coffeevan.coffeevan.db.DatabaseConnection;
import com.yurii.coffeevan.coffeevan.db.VanService;
import com.yurii.coffeevan.coffeevan.util.LoggerUtil;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;
import java.util.Optional;


// Керування головною сторінкою
public class VanController {
    @FXML private TableView<Coffee> coffeeTable;
    @FXML private ComboBox<String> typeFilter;
    @FXML private ChoiceBox<String> qualityTypeChoice;
    @FXML private Label qualityLabel;
    @FXML private ChoiceBox<String> sortChoice;

    @FXML private TextField nameField;
    @FXML private ComboBox<String> typeField;
    @FXML private TextField volumeField;
    @FXML private TextField priceField;
    @FXML private TextField weightField;
    @FXML private Slider qualityField;
    @FXML private Label qualityValueLabel;
    @FXML private Slider coffeeQualitySlider;
    @FXML private Label coffeeQualityLabel;
    @FXML private Button addButton;
    @FXML private Button loadButton;
    @FXML private Button clearButton;
    @FXML private Button saveButton;
    @FXML private Button deleteButton;
    @FXML private Button editButton;
    @FXML private Label totalVolumeLabel;

    @FXML private TableColumn<Coffee, String> nameColumn;
    @FXML private TableColumn<Coffee, String> typeColumn;
    @FXML private TableColumn<Coffee, Integer> volumeColumn;
    @FXML private TableColumn<Coffee, Double> priceColumn;
    @FXML private TableColumn<Coffee, Integer> weightColumn;
    @FXML private TableColumn<Coffee, Integer> qualityColumn;
    @FXML private TableColumn<Coffee, String> qualityTypeColumn;
    @FXML private TableColumn<Coffee, Integer> quantityColumn;

    private Van van;                                // Модель фургону
    private ObservableList<Coffee> allCoffee;       // Список усієї кави у фургоні
    private FilteredList<Coffee> filteredCoffee;    // Відфільтрований список кави
    private SortedList<Coffee> sortedCoffee;        // Відсортований список кави
    private VanService vanService;                  // Обслуговує роботу з БД

    @FXML
    public void initialize() {
        try {
            van = new Van();
            vanService = new VanService(DatabaseConnection.getConnection());
            allCoffee = FXCollections.observableArrayList();                // Список кави який можна оновлювати динамічно
            filteredCoffee = new FilteredList<>(allCoffee, p -> true);
            sortedCoffee = new SortedList<>(filteredCoffee);
            
            coffeeTable.setItems(sortedCoffee);         // Таблиця відображає каву з урахуванням фільтрації та сортуванян
            // Додаємо прив'язку компаратора
            sortedCoffee.comparatorProperty().bind(coffeeTable.comparatorProperty());

            // Вказує яку властивість Coffee відображати у відповідній колонці
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
            volumeColumn.setCellValueFactory(new PropertyValueFactory<>("volume"));
            priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
            weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));
            qualityColumn.setCellValueFactory(new PropertyValueFactory<>("quality"));
            quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            qualityTypeColumn.setCellValueFactory(data ->
                new SimpleStringProperty(getQualityType(data.getValue().getQuality())));

            // Заповнення списку вибору
            typeField.setItems(FXCollections.observableArrayList(
                "Зернова", 
                "Мелена", 
                "Розчинна (банка)", 
                "Розчинна (пакетик)"
            ));

            typeFilter.setItems(FXCollections.observableArrayList(
                "Всі типи",
                "Зернова", 
                "Мелена", 
                "Розчинна (банка)", 
                "Розчинна (пакетик)"
            ));
            typeFilter.setValue("Всі типи");

            qualityTypeChoice.setItems(FXCollections.observableArrayList(
                "Всі типи",
                "Низька якість",
                "Нижче середньої",
                "Середня",
                "Висока",
                "Преміум"
            ));
            qualityTypeChoice.setValue("Всі типи");

            sortChoice.setItems(FXCollections.observableArrayList(
                "Без сортування",
                "Ціна/вага (зростання)",
                "Ціна/вага (спадання)"
            ));
            sortChoice.setValue("Без сортування");

            // Показують поточне значення слайдерів
            qualityField.setValue(1);
            coffeeQualitySlider.setValue(50);

            qualityField.valueProperty().addListener((obs, oldVal, newVal) ->
                qualityValueLabel.setText(String.valueOf(newVal.intValue())));

            coffeeQualitySlider.valueProperty().addListener((obs, oldVal, newVal) ->
                coffeeQualityLabel.setText(String.valueOf(newVal.intValue())));

            // Призміні значень у фільтрах викликається оновлення списку
            typeFilter.setOnAction(e -> applyFilter());
            qualityTypeChoice.setOnAction(e -> applyFilter());
            sortChoice.setOnAction(e -> applySorting());

            // Прив'язка кнопок до їх методів
            saveButton.setOnAction(event -> handleSaveVan());
            clearButton.setOnAction(event -> handleClearVan());
            loadButton.setOnAction(event -> handleShowVans());
            deleteButton.setDisable(true);
            editButton.setDisable(true);

            // Кнопки Видалити та Редагувати активуються тоді коли вибраний якийсь елемент таблиці
            coffeeTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    deleteButton.setDisable(newSelection == null);
                    editButton.setDisable(newSelection == null);
                }
            );

            LoggerUtil.info("Application initialized successfully");
        } catch (Exception e) {
            LoggerUtil.error("Failed to initialize application", e);
            showError("Initialization Error", "Failed to initialize the application");
        }
    }

    // Перетворює числове значення якості у текстовий опис
    private String getQualityType(int quality) {
        if (quality >= 1 && quality <= 20) return "Низька якість";
        if (quality >= 21 && quality <= 40) return "Нижче середньої";
        if (quality >= 41 && quality <= 60) return "Середня";
        if (quality >= 61 && quality <= 80) return "Висока";
        if (quality >= 81 && quality <= 100) return "Преміум";
        return "Невідома якість";
    }

    // Фільтрація списку кави за обраними критеріями
    private void applyFilter() {
        String selectedType = typeFilter.getValue();
        String selectedQualityType = qualityTypeChoice.getValue();
        
        filteredCoffee.setPredicate(coffee -> {
            boolean typeMatch = selectedType == null || 
                              selectedType.equals("Всі типи") || 
                              coffee.getType().equals(selectedType);
            
            boolean qualityMatch = selectedQualityType == null || 
                                 selectedQualityType.equals("Всі типи") || 
                                 getQualityType(coffee.getQuality()).equals(selectedQualityType);
            
            return typeMatch && qualityMatch;
        });
        
        updateTotalVolume();
    }

    // Сортування списку кави ціна/вага
    private void applySorting() {
        String sortType = sortChoice.getValue();
        if (sortType == null) return;

        // Очищаємо попереднє сортування
        coffeeTable.getSortOrder().clear();
        sortedCoffee.comparatorProperty().unbind();
        
        switch (sortType) {
            case "Ціна/вага (зростання)" -> {
                sortedCoffee.setComparator((c1, c2) -> 
                    Double.compare(c1.getPrice() / c1.getWeight(), c2.getPrice() / c2.getWeight()));
            }
            case "Ціна/вага (спадання)" -> {
                sortedCoffee.setComparator((c1, c2) -> 
                    Double.compare(c2.getPrice() / c2.getWeight(), c1.getPrice() / c1.getWeight()));
            }
            default -> {
                // Повертаємо стандартне сортування по колонках
                sortedCoffee.comparatorProperty().bind(coffeeTable.comparatorProperty());
            }
        }
    }

    // Оновлює загальний об'єм кави в залежності від відображення в таблиці
    private void updateTotalVolume() {
        int totalVolume = filteredCoffee.stream()
                .mapToInt(coffee -> coffee.getVolume() * coffee.getQuantity())
                .sum();
        totalVolumeLabel.setText(String.format("%d мл", totalVolume));
    }

    // При натисненні кнопки 'Додати каву' зчитує введені дані, створює лб'єкти кави і додає його у фургон якщо всі умови дотримані
    @FXML
    private void handleAddCoffee() {
        try {
            if (nameField.getText().isEmpty()) {
                LoggerUtil.warn("Attempt to add coffee with empty name");
                showAlert(Alert.AlertType.WARNING, "Попередження", "Введіть назву кави");
                return;
            }
            
            if (typeField.getValue() == null) {
                LoggerUtil.warn("Attempt to add coffee with no type selected");
                showAlert(Alert.AlertType.WARNING, "Попередження", "Виберіть тип кави");
                return;
            }

            if (volumeField.getText().isEmpty() || priceField.getText().isEmpty() || weightField.getText().isEmpty()) {
                LoggerUtil.warn("Attempt to add coffee with empty required fields");
                showAlert(Alert.AlertType.WARNING, "Попередження", "Заповніть всі поля");
                return;
            }

            if (qualityField == null || coffeeQualitySlider == null) {
                LoggerUtil.error("Quality sliders not initialized");
                showAlert(Alert.AlertType.ERROR, "Помилка", "Помилка ініціалізації слайдерів");
                return;
            }


            String name = nameField.getText();
            String type = typeField.getValue();
            int volume = Integer.parseInt(volumeField.getText());
            double price = Double.parseDouble(priceField.getText());
            int weight = Integer.parseInt(weightField.getText());
            int quantity = (int) qualityField.getValue();
            int quality = (int) coffeeQualitySlider.getValue();

            Coffee coffee = switch (type) {
                case "Зернова" -> new BeanCoffee(name, volume, price, weight, quality, quantity);
                case "Мелена" -> new GroundCoffee(name, volume, price, weight, quality, quantity);
                case "Розчинна (банка)" -> new InstantJarCoffee(name, volume, price, weight, quality, quantity);
                case "Розчинна (пакетик)" -> new InstantPacketCoffee(name, volume, price, weight, quality, quantity);
                default -> throw new IllegalArgumentException("Невідомий тип кави");
            };

            int newTotalVolume = van.getTotalVolume() + (coffee.getVolume() * coffee.getQuantity());
            if (newTotalVolume > van.getMaxCapacity()) {
                LoggerUtil.warn("Attempt to exceed van capacity: " + newTotalVolume + " ml (max: " + van.getMaxCapacity() + " ml)");
                showAlert(Alert.AlertType.WARNING, "Перевищено обʼєм фургона", 
                    String.format("Неможливо додати каву. Новий загальний об'єм (%d мл) перевищить місткість фургона (%d мл).",
                        newTotalVolume, (int)van.getMaxCapacity()));
                return;
            }

            van.addCoffee(coffee);
            allCoffee.setAll(van.getAllCoffee());
            applyFilter();
            clearInputFields();
            LoggerUtil.info("Added new coffee: " + name + " (" + type + "), volume: " + volume + "ml, quantity: " + quantity);
        } catch (NumberFormatException e) {
            LoggerUtil.error("Invalid number format in input fields", e);
            showAlert(Alert.AlertType.ERROR, "Помилка", "Перевірте правильність числових значень");
        } catch (Exception e) {
            LoggerUtil.error("Failed to add coffee", e);
            showError("Add Coffee Error", "Failed to add new coffee");
        }
    }

    // Обробник натискання кнопки "Редагувати"
    @FXML
    private void handleEdit() {
        Coffee selectedCoffee = coffeeTable.getSelectionModel().getSelectedItem();
        if (selectedCoffee != null) {
            // Заповнюємо поля форми значеннями вибраної кави
            nameField.setText(selectedCoffee.getName());
            typeField.setValue(selectedCoffee.getType());
            volumeField.setText(String.valueOf(selectedCoffee.getVolume()));
            priceField.setText(String.valueOf(selectedCoffee.getPrice()));
            weightField.setText(String.valueOf(selectedCoffee.getWeight()));
            qualityField.setValue(selectedCoffee.getQuality());
            
            // Змінюємо поведінку кнопки "Додати" на "Оновити"
            addButton.setText("Оновити");
            addButton.setOnAction(event -> handleUpdate(selectedCoffee));
            
            LoggerUtil.info("Editing coffee: " + selectedCoffee.getName());
        }
    }

    // Обробник оновлення кави
    private void handleUpdate(Coffee oldCoffee) {
        try {
            // Отримуємо значення з полів форми
            String name = nameField.getText().trim();
            String type = typeField.getValue();
            int volume = Integer.parseInt(volumeField.getText().trim());
            double price = Double.parseDouble(priceField.getText().trim());
            int weight = Integer.parseInt(weightField.getText().trim());
            int quality = (int) coffeeQualitySlider.getValue();
            
            // Валідація даних
            if (name.isEmpty() || type == null) {
                showAlert(Alert.AlertType.WARNING, "Попередження", "Будь ласка, заповніть всі поля!");
                return;
            }
            
            // Створюємо новий об'єкт кави з оновленими даними
            Coffee updatedCoffee = switch (type) {
                case "Зернова" -> new BeanCoffee(name, volume, price, weight, quality, oldCoffee.getQuantity());
                case "Мелена" -> new GroundCoffee(name, volume, price, weight, quality, oldCoffee.getQuantity());
                case "Розчинна (банка)" -> new InstantJarCoffee(name, volume, price, weight, quality, oldCoffee.getQuantity());
                case "Розчинна (пакетик)" -> new InstantPacketCoffee(name, volume, price, weight, quality, oldCoffee.getQuantity());
                default -> throw new IllegalArgumentException("Невідомий тип кави");
            };
            
            // Оновлюємо каву у фургоні
            van.removeCoffee(oldCoffee);
            van.addCoffee(updatedCoffee);
            
            // Оновлюємо таблицю
            allCoffee.setAll(van.getAllCoffee());
            updateTotalVolume();
            
            // Очищаємо форму і повертаємо кнопку до початкового стану
            clearForm();
            addButton.setText("Додати");
            addButton.setOnAction(event -> handleAddCoffee());
            
            LoggerUtil.info("Updated coffee: " + name);
            showAlert(Alert.AlertType.INFORMATION, "Успіх", "Каву успішно оновлено!");
            
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Помилка", "Будь ласка, введіть коректні числові значення!");
        } catch (Exception e) {
            LoggerUtil.error("Failed to update coffee", e);
            showAlert(Alert.AlertType.ERROR, "Помилка", "Не вдалося оновити каву: " + e.getMessage());
        }
    }

    // Метод для очищення форми
    private void clearForm() {
        nameField.clear();
        typeField.setValue(null);
        volumeField.clear();
        priceField.clear();
        weightField.clear();
        coffeeQualitySlider.setValue(50);
    }

    // Очищуємо поля вводу
    private void clearInputFields() {
        nameField.clear();
        typeField.setValue(null);
        volumeField.clear();
        priceField.clear();
        weightField.clear();
        qualityField.setValue(1);
        coffeeQualitySlider.setValue(50);
        qualityValueLabel.setText("1");
        coffeeQualityLabel.setText("50");
    }

    // Зберігаємо фургон в бд
    private void handleSaveVan() {
        try {
            int vanId = vanService.saveVan(van);
            LoggerUtil.info("Van saved successfully with ID: " + vanId + ", total volume: " + van.getTotalVolume() + "ml");
            showAlert(Alert.AlertType.INFORMATION, "Успіх", "Фургон успішно збережено в базі даних!");
        } catch (Exception e) {
            LoggerUtil.error("Failed to save van", e);
            showError("Save Van Error", "Failed to save van");
        }
    }

    // Очищення всіх даних з таблиці
    private void handleClearVan() {
        LoggerUtil.info("Clearing van with " + van.getAllCoffee().size() + " coffee items");
        van = new Van();
        allCoffee.clear();
        updateTotalVolume();
        showAlert(Alert.AlertType.INFORMATION, "Очищено", "Фургон успішно очищено!");
    }

    // Показуємо нове вікно з усіма фургонами які вже завантаженні і існують в бд
    private void handleShowVans() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("vans-view.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Фургони з завантаженою кавою");
            stage.setScene(new Scene(root));
            stage.show();
            LoggerUtil.info("Opened vans view window");
        } catch (IOException e) {
            LoggerUtil.error("Failed to open vans view", e);
            showError("Show Vans Error", "Failed to show vans");
        }
    }

    // Видаляємо обрану колонку з кавою
    @FXML
    private void handleDeleteSelected() {
        Coffee selectedCoffee = coffeeTable.getSelectionModel().getSelectedItem();
        if (selectedCoffee != null) {
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Підтвердження видалення");
            confirmDialog.setHeaderText(null);
            confirmDialog.setContentText("Ви впевнені, що хочете видалити вибрану каву?");

            Optional<ButtonType> result = confirmDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                van.removeCoffee(selectedCoffee);
                allCoffee.setAll(van.getAllCoffee());
                updateTotalVolume();
                LoggerUtil.info("Deleted coffee: " + selectedCoffee.getName() + " from van");
                showAlert(Alert.AlertType.INFORMATION, "Видалено", "Каву успішно видалено з фургону!");
            }
        }
    }

    // Метод для показу вікон сповіщень
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Метод для показу вікон помилок
    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Метод для показу вікон попереджень
    private void showWarning(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // // Метод для показу вікон підтвердження
    private boolean showConfirmation(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
}
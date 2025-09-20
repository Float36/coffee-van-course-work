package com.yurii.coffeevan.coffeevan;

import com.yurii.coffeevan.coffeevan.model.Coffee;
import com.yurii.coffeevan.coffeevan.util.LoggerUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

// Керування сторінкою вмісту фургону
public class VanCoffeeController {
    
    @FXML
    private Label vanInfoLabel;
    
    @FXML
    private Label totalVolumeLabel;
    
    @FXML
    private Label totalWeightLabel;
    
    @FXML
    private Label averageQualityLabel;
    
    @FXML
    private Label totalPriceLabel;
    
    @FXML
    private TableView<Coffee> coffeeTable;
    
    @FXML
    private ComboBox<String> typeFilter;
    
    @FXML
    private ChoiceBox<String> qualityTypeChoice;
    
    @FXML
    private Button resetFiltersButton;
    
    @FXML
    private Button closeButton;
    
    @FXML
    private TableColumn<Coffee, String> nameColumn;
    
    @FXML
    private TableColumn<Coffee, String> typeColumn;
    
    @FXML
    private TableColumn<Coffee, Integer> volumeColumn;
    
    @FXML
    private TableColumn<Coffee, Double> priceColumn;
    
    @FXML
    private TableColumn<Coffee, Integer> weightColumn;
    
    @FXML
    private TableColumn<Coffee, Integer> qualityColumn;
    
    @FXML
    private TableColumn<Coffee, String> qualityTypeColumn;
    
    @FXML
    private TableColumn<Coffee, Integer> quantityColumn;
    
    private ObservableList<Coffee> allCoffee = FXCollections.observableArrayList();
    private FilteredList<Coffee> filteredCoffee;
    private SortedList<Coffee> sortedCoffee;
    
    @FXML
    public void initialize() {
        try {
            // Ініціалізація списків
            filteredCoffee = new FilteredList<>(allCoffee, p -> true);
            sortedCoffee = new SortedList<>(filteredCoffee);
            sortedCoffee.comparatorProperty().bind(coffeeTable.comparatorProperty());
            
            // Налаштування таблиці
            coffeeTable.setItems(sortedCoffee);
            
            // Налаштування колонок
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
            volumeColumn.setCellValueFactory(new PropertyValueFactory<>("volume"));
            priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
            weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));
            qualityColumn.setCellValueFactory(new PropertyValueFactory<>("quality"));
            qualityTypeColumn.setCellValueFactory(data -> 
                new SimpleStringProperty(getQualityType(data.getValue().getQuality())));
            quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            
            // Налаштування фільтра типів
            typeFilter.setItems(FXCollections.observableArrayList(
                "Всі типи",
                "Зернова", 
                "Мелена", 
                "Розчинна (банка)", 
                "Розчинна (пакетик)"
            ));
            typeFilter.setValue("Всі типи");
            
            // Налаштування фільтра за типом якості
            qualityTypeChoice.setItems(FXCollections.observableArrayList(
                "Всі типи",
                "Низька якість",
                "Нижче середньої",
                "Середня",
                "Висока",
                "Преміум"
            ));
            qualityTypeChoice.setValue("Всі типи");
            
            // Налаштування слухачів подій
            typeFilter.setOnAction(e -> applyFilter());
            qualityTypeChoice.setOnAction(e -> applyFilter());
            resetFiltersButton.setOnAction(e -> resetFilters());

            LoggerUtil.info("Coffee contents view initialized successfully");
        } catch (Exception e) {
            LoggerUtil.error("Failed to initialize coffee contents view", e);
        }
    }

    // очищує задані фільтри
    private void resetFilters() {
        typeFilter.setValue("Всі типи");
        qualityTypeChoice.setValue("Всі типи");
        LoggerUtil.info("Filters reset to default values");
        applyFilter();
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
        
        updateStatistics();
    }

    // Оновлення статистики про вміст фургона
    private void updateStatistics() {
        int totalVolume = filteredCoffee.stream()
                .mapToInt(coffee -> coffee.getVolume() * coffee.getQuantity())
                .sum();
        
        int totalWeight = filteredCoffee.stream()
                .mapToInt(coffee -> coffee.getWeight() * coffee.getQuantity())
                .sum();
        
        double averageQuality = filteredCoffee.stream()
                .mapToInt(Coffee::getQuality)
                .average()
                .orElse(0.0);
        
        double totalPrice = filteredCoffee.stream()
                .mapToDouble(coffee -> coffee.getPrice() * coffee.getQuantity())
                .sum();
        
        totalVolumeLabel.setText(String.format("Загальний об'єм: %d мл", totalVolume));
        totalWeightLabel.setText(String.format("Загальна вага: %d г", totalWeight));
        averageQualityLabel.setText(String.format("Середня якість: %.1f", averageQuality));
        totalPriceLabel.setText(String.format("Загальна вартість: %.2f грн", totalPrice));
        
        LoggerUtil.debug(String.format("Updated statistics - Volume: %d ml, Weight: %d g, Avg Quality: %.1f, Total Price: %.2f UAH",
            totalVolume, totalWeight, averageQuality, totalPrice));
    }

    // Змінює дані на обраний фургон
    public void setVanInfo(VansController.VanEntry van) {
        vanInfoLabel.setText(String.format("Фургон #%d | Створено: %s", 
            van.getId(), 
            van.getCreatedAt().toString().replace(".0", "")));
        LoggerUtil.info("Set van info for van #" + van.getId());
    }

    // Оновлює список кави
    public void setCoffeeList(ObservableList<Coffee> coffee) {
        allCoffee.setAll(coffee);
        applyFilter();
        updateStatistics();
        LoggerUtil.info("Loaded " + coffee.size() + " coffee items");
    }
    
    @FXML
    private void handleClose() {
        ((Stage) closeButton.getScene().getWindow()).close();
        LoggerUtil.info("Closed coffee contents window");
    }
} 
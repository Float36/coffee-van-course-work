package com.yurii.coffeevan.coffeevan;

import com.yurii.coffeevan.coffeevan.util.LoggerUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.Parent;

import java.io.IOException;

// запуск програми
public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            LoggerUtil.info("Starting Coffee Van application...");
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 1200, 700);
            primaryStage.setTitle("Фургон кави");
            primaryStage.setScene(scene);
            
            // Завантаження іконки з ресурсів
            Image icon = new Image(getClass().getResourceAsStream("img/icon.png"));
            if (icon != null) {
                primaryStage.getIcons().add(icon);
            }

            primaryStage.show();

            LoggerUtil.info("Application UI loaded successfully");
        } catch (Exception e) {
            LoggerUtil.error("Failed to start application", e);
            throw e;
        }
    }

    public static void main(String[] args) {
        try {
            LoggerUtil.info("Initializing application...");
            launch(args);
        } catch (Exception e) {
            LoggerUtil.error("Critical error during application initialization", e);
            throw e;
        }
    }
}
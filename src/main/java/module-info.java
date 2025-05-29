module com.yurii.coffeevan.coffeevan {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.logging.log4j;
    requires java.mail;

    opens com.yurii.coffeevan.coffeevan to javafx.fxml;
    opens com.yurii.coffeevan.coffeevan.model to javafx.base;

    exports com.yurii.coffeevan.coffeevan;
    exports com.yurii.coffeevan.coffeevan.model;
    exports com.yurii.coffeevan.coffeevan.db;
    exports com.yurii.coffeevan.coffeevan.util;
}
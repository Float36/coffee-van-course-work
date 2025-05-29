package com.yurii.coffeevan.coffeevan.model;

import com.yurii.coffeevan.coffeevan.model.Coffee;

// Похідний клас Зернова кава
public class BeanCoffee extends Coffee {
    public BeanCoffee(String name, int volume, double price, int weight, int quality, int quantity) {
        super(name, "Зернова", volume, price, weight, quality, quantity);
    }
}

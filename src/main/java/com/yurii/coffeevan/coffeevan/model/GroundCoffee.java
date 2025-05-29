package com.yurii.coffeevan.coffeevan.model;

import com.yurii.coffeevan.coffeevan.model.Coffee;

// Похідний клас Мелена кава
public class GroundCoffee extends Coffee {
    public GroundCoffee(String name, int volume, double price, int weight, int quality, int quantity) {
        super(name, "Мелена", volume, price, weight, quality, quantity);
    }
}
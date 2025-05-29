package com.yurii.coffeevan.coffeevan.model;

import com.yurii.coffeevan.coffeevan.model.Coffee;

// Похідний клас Розчинна кава в банках
public class InstantJarCoffee extends Coffee {
    public InstantJarCoffee(String name, int volume, double price, int weight, int quality, int quantity) {
        super(name, "Розчинна (банка)", volume, price, weight, quality, quantity);
    }
}

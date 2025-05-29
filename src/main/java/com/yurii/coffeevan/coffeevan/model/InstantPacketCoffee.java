package com.yurii.coffeevan.coffeevan.model;

import com.yurii.coffeevan.coffeevan.model.Coffee;

// Похідний клас Розчинна кава в пакетиках
public class InstantPacketCoffee extends Coffee {
    public InstantPacketCoffee(String name, int volume, double price, int weight, int quality, int quantity) {
        super(name, "Розчинна (пакетик)", volume, price, weight, quality, quantity);
    }
}
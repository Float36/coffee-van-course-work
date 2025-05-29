package com.yurii.coffeevan.coffeevan.model;

// Батьківський клас для зберігання даних кави
public class Coffee {
    protected String name;
    protected String type;
    protected int volume;           // об'єм
    protected double price;
    protected int weight;
    protected int quality;          // кількість
    protected int quantity;         // якість

    //
    public Coffee(String name, String type, int volume, double price, int weight, int quality, int quantity) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Ім'я кави не може бути порожнім");
        }
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Тип кави не може бути порожнім");
        }
        if (volume <= 0) {
            throw new IllegalArgumentException("Об'єм має бути більше 0");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Ціна має бути більше 0");
        }
        if (weight <= 0) {
            throw new IllegalArgumentException("Вага має бути більше 0");
        }
        if (quality < 0 || quality > 100) {
            throw new IllegalArgumentException("Якість має бути від 0 до 100");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Кількість має бути більше 0");
        }
        
        this.name = name;
        this.type = type;
        this.volume = volume;
        this.price = price;
        this.weight = weight;
        this.quality = quality;
        this.quantity = quantity;
    }

    public String getName() { return name; }
    public String getType() { return type; }
    public int getVolume() { return volume; }
    public double getPrice() { return price; }
    public int getWeight() { return weight; }
    public int getQuality() { return quality; }
    public int getQuantity() { return quantity; }

    // Загальний об'єм кави разом з кількістю
    public int getCoffeeVolume(){
        return this.quantity * this.volume;
    }

    // Сеттери
    public void setName(String name) { this.name = name; }
    public void setType(String type) { this.type = type; }
    public void setVolume(int volume) { this.volume = volume; }
    public void setPrice(double price) { this.price = price; }
    public void setWeight(int weight) { this.weight = weight; }
    public void setQuality(int quality) { this.quality = quality; }



    public double getPriceToWeightRatio(){
        return weight == 0 ? 0 : price / weight;
    }
}

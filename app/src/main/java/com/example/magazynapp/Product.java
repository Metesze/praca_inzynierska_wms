package com.example.magazynapp;

import java.io.Serializable;

public class Product implements Serializable {
    private String name;
    private double price;
    private int quantity;
    private String barcode;

    // Pusty konstruktor wymagany przez Firestore do deserializacji
    public Product() {}

    // Konstruktor pełny
    public Product(String name, double price, int quantity, String barcode) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.barcode = barcode;
    }

    // Gettery
    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getBarcode() {
        return barcode;
    }

    // Settery
    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    @Override
    public String toString() {
        return "Nazwa: " + name +
                ", Cena: " + price +
                ", Ilość: " + quantity +
                ", Kod kreskowy: " + barcode;
    }
}

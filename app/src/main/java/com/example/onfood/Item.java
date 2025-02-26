package com.example.onfood;

public class Item {
    private String id;
    private String name;
    private double price;
    private String category; // Category field
    private String imageUrl;
    private String description; // New field for description
    private int quantity; // Field to store the quantity

    public Item() {
        // Firestore requires a no-arg constructor
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() { // Getter for description
        return description;
    }

    public void setDescription(String description) { // Setter for description
        this.description = description;
    }

    public int getQuantity() { // Getter for quantity
        return quantity;
    }

    public void setQuantity(int quantity) { // Setter for quantity
        this.quantity = quantity;
    }

    public String displayItemDetails() {
        return "Item ID: " + id + "\n" +
                "Name: " + name + "\n" +
                "Price: ₹" + price + "\n" +
                "Category: " + category + "\n" +
                "Description: " + description + "\n" +
                "Quantity: " + quantity; // Display quantity
    }
}
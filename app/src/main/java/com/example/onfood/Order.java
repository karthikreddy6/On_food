package com.example.onfood;

public class Order {
    private String orderId;
    private double amount;
    private String orderDate;
    private String orderTime;

    public Order(String orderId, double amount, String orderDate, String orderTime) {
        this.orderId = orderId;
        this.amount = amount;
        this.orderDate = orderDate;
        this.orderTime = orderTime;
    }

    public String getOrderId() {
        return orderId;
    }

    public double getAmount() {
        return amount;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getOrderTime() {
        return orderTime;
    }
}
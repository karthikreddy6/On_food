package com.example.onfood;

public class Order {
    private String orderId;
    private double amount;
    private String orderDate;
    private String orderTime;
    private String status;

    public Order(String orderId, String orderDate, String orderTime, double amount, String status) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.orderTime = orderTime;
        this.amount = amount;
        this.status = (status != null) ? status : "Unknown"; // Ensure status is never null
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
    public String getStatus() { return status; }

}
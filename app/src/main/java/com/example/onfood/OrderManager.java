package com.example.onfood;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;import android.content.Context;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class OrderManager {
    private DatabaseReference ordersRef;
    private CartManager cartManager;

    // Constructor
    public OrderManager(Context context) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ordersRef = database.getReference("Orders");  // Reference to "Orders" node
        cartManager = CartManager.getInstance(context);  // Get CartManager instance
    }

    // Place order in Firebase Realtime Database
    public void placeOrder(String userId) {
        // Generate a unique Order ID
        String orderId = ordersRef.push().getKey();  // Generate unique order ID

        if (orderId == null) {
            return;  // If orderId is null, something went wrong with push() call.
        }

        // Get cart items and total amount
        Map<String, CartItem> cartItems = cartManager.getCartItemsWithQuantities();
        double totalAmount = cartManager.getTotalAmount();

        if (cartItems.isEmpty()) {
            // If cart is empty, do not place the order
            return;
        }

        // Create order details
        Map<String, Object> orderDetails = new HashMap<>();
        orderDetails.put("userId", userId);
        orderDetails.put("amount", totalAmount);
        orderDetails.put("items", cartItems);  // Add cart items

        // Store the order in Firebase under Orders/Order ID
        ordersRef.child(orderId).setValue(orderDetails)
                .addOnSuccessListener(aVoid -> {
                    // Successfully stored the order
                    System.out.println("Order placed successfully!");
                })
                .addOnFailureListener(e -> {
                    // Failed to store the order
                    System.err.println("Error placing order: " + e.getMessage());
                });
    }
}

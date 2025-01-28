package com.example.onfood;

import android.content.Context;
import android.icu.text.SimpleDateFormat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
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
    public void placeOrder(String userId, OnOrderPlacedListener listener) {
        // Generate a unique Order ID
        String orderId = ordersRef.push().getKey();  // Generate unique order ID

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());
        SimpleDateFormat TimeFormat = new SimpleDateFormat(" hh.mm a", Locale.getDefault());
        String formattedTime = TimeFormat.format(new Date());
        String formattedDate = dateFormat.format(new Date());

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
        orderDetails.put("orderDate", formattedDate);
        orderDetails.put("orderTime", formattedTime);
        orderDetails.put("status","confirmed");

        // Create a nested map to store items under "items" node
        Map<String, Object> itemsMap = new HashMap<>();
        int itemIndex = 1;

        for (Map.Entry<String, CartItem> entry : cartItems.entrySet()) {
            CartItem cartItem = entry.getValue();
            Map<String, Object> itemDetails = new HashMap<>();
            itemDetails.put("name", cartItem.getItem().getName());
            itemDetails.put("price", cartItem.getItem().getPrice());
            itemDetails.put("quantity", cartItem.getQuantity());
            // Assign each item a unique key like "item1", "item2", etc.
            itemsMap.put("item" + itemIndex, itemDetails);
            itemIndex++;
        }
        // Add the items map to the order details
        orderDetails.put("items", itemsMap);

        // Store the order in Firebase under Orders/Order ID
        ordersRef.child(orderId).setValue(orderDetails)
                .addOnSuccessListener(aVoid -> {
                    // Successfully stored the order
                    System.out.println("Order placed successfully!");
                    listener.onOrderPlaced(orderId);  // Notify listener with order ID
                })
                .addOnFailureListener(e -> {
                    // Failed to store the order
                    System.err.println("Error placing order: " + e.getMessage());
                });
    }

    // Define interface for order placed listener
    public interface OnOrderPlacedListener {
        void onOrderPlaced(String orderId);
    }
}
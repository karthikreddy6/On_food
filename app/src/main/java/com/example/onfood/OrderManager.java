package com.example.onfood;

import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class OrderManager {
    private DatabaseReference ordersRef;
    private CartManager cartManager;
    private SharedPreferences sharedPreferences;

    // Constructor
    public OrderManager(Context context) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ordersRef = database.getReference("Orders");  // Reference to "Orders" node
        cartManager = CartManager.getInstance(context);  // Get CartManager instance
        sharedPreferences = context.getSharedPreferences("UserData", Context.MODE_PRIVATE);

    }

    // Place order in Firebase Realtime Database
    public void placeOrder(String userId, OnOrderPlacedListener listener) {
        // Generate a unique Order ID
        String orderId = ordersRef.push().getKey();  // Generate unique order ID

        String userName = sharedPreferences.getString("name", "Unknown User");
        String userPhone = sharedPreferences.getString("phone", "No Phone");

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
        orderDetails.put("username", userName);
        orderDetails.put("userPhone", userPhone);
        orderDetails.put("amount", totalAmount);
        orderDetails.put("orderDate", formattedDate);
        orderDetails.put("orderTime", formattedTime);
        orderDetails.put("status", "confirmed");
        orderDetails.put("timestamp", ServerValue.TIMESTAMP);

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
                    updateItemQuantities(cartItems);
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
    private void updateItemQuantities(Map<String, CartItem> cartItems) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference itemsRef = firestore.collection("MenuItem");

        for (Map.Entry<String, CartItem> entry : cartItems.entrySet()) {
            String itemId = entry.getKey();
            CartItem cartItem = entry.getValue();

            System.out.println("🛠️ Attempting to update item: " + itemId);

            // Get the document reference for the specific item
            DocumentReference itemDocRef = itemsRef.document(itemId);

            itemDocRef.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        System.out.println("📥 Firestore returned data for item: " + itemId);

                        if (documentSnapshot.exists()) {
                            Long currentQuantity = documentSnapshot.getLong("quantity"); // Retrieve quantity field as Long

                            if (currentQuantity != null) {
                                System.out.println("✅ Current stock: " + currentQuantity);
                                System.out.println("🛠️ Raw data retrieved for item: " + itemId + " - " + documentSnapshot.getData());

                                System.out.println("🛒 Quantity ordered: " + cartItem.getQuantity());

                                long newQuantity = currentQuantity - cartItem.getQuantity();
                                System.out.println("📦 New quantity to set: " + newQuantity);

                                if (newQuantity >= 0) {
                                    // Update the quantity in Firestore
                                    itemDocRef.update("quantity", newQuantity)
                                            .addOnSuccessListener(aVoid -> {
                                                System.out.println("✅ Quantity updated successfully for item: " + itemId);
                                                cartManager.updateItemQuantity(cartItem.getItem(), (int) newQuantity);
                                            })
                                            .addOnFailureListener(e -> {
                                                System.err.println("❌ Failed to update quantity for item: " + itemId + ". Error: " + e.getMessage());
                                            });
                                } else {
                                    System.err.println("⚠️ Not enough stock to fulfill order for item: " + itemId);
                                }
                            } else {
                                System.err.println("⚠️ Quantity field is null for item: " + itemId);
                            }
                        } else {
                            System.err.println("⚠️ Document doesn't exist for item: " + itemId);
                        }
                    })
                    .addOnFailureListener(e -> {
                        System.err.println("🔥 Firestore query failed for item: " + itemId + ". Error: " + e.getMessage());
                    });
        }
    }

}
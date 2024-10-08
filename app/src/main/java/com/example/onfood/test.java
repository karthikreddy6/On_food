//package com.example.onfood;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import java.lang.reflect.Type;
//import java.util.HashMap;
//import java.util.Map;
//
//public class CartManager {
//    private static final String CART_PREFS = "cart_prefs";  // SharedPreferences file name
//    private SharedPreferences sharedPreferences;
//    private Gson gson = new Gson();
//    private static CartManager instance;
//
//    // Singleton to ensure only one instance of CartManager is used across the app
//    public static synchronized CartManager getInstance(Context context) {
//        if (instance == null) {
//            instance = new CartManager(context);
//        }
//        return instance;
//    }
//
//    private CartManager(Context context) {
//        sharedPreferences = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE);
//    }
//
//    // Add an item to the cart or increase its quantity by 1
//    public void addToCart(Item item) {
//        String itemId = item.getId();
//        Map<String, CartItem> cart = getCartItemsWithQuantities();
//
//        if (cart.containsKey(itemId)) {
//            // If the item already exists, increase its quantity
//            CartItem cartItem = cart.get(itemId);
//            cartItem.setQuantity(cartItem.getQuantity() + 1);
//        } else {
//            // Add new item with a quantity of 1
//            cart.put(itemId, new CartItem(item, 1));
//        }
//
//        saveCart(cart);  // Save the updated cart to SharedPreferences
//    }
//
//    // Calculate total amount of all items in the cart
//    public double getTotalAmount() {
//        Map<String, CartItem> cartItems = getCartItemsWithQuantities();
//        double totalAmount = 0.0;
//
//        for (CartItem cartItem : cartItems.values()) {
//            totalAmount += cartItem.getItem().getPrice() * cartItem.getQuantity();
//        }
//
//        return totalAmount;
//    }
//
//    // Get cart items along with their quantities
//    public Map<String, CartItem> getCartItemsWithQuantities() {
//        String cartJson = sharedPreferences.getString("cart_items", "");
//        if (!cartJson.isEmpty()) {
//            Type type = new TypeToken<Map<String, CartItem>>() {}.getType();
//            return gson.fromJson(cartJson, type);
//        }
//        return new HashMap<>();  // Return empty cart if no items found
//    }
//
//    // Other existing methods...
//}

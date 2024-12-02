package com.example.onfood;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartManager {
    private static final String CART_PREFS = "cart_prefs";
    private SharedPreferences sharedPreferences;
    private Gson gson = new Gson();
    private static CartManager instance;
    private List<CartChangeListener> listeners = new ArrayList<>();

    public static synchronized CartManager getInstance(Context context) {
        if (instance == null) {
            instance = new CartManager(context);
        }
        return instance;
    }

    private CartManager(Context context) {
        sharedPreferences = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE);
    }

    public void registerCartChangeListener(CartChangeListener listener) {
        listeners.add(listener);
    }

    public void unregisterCartChangeListener(CartChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyCartChange() {
        for (CartChangeListener listener : listeners) {
            listener.onCartChanged();
        }
    }

    public void addToCart(Item item) {
        String itemId = item.getId();
        Map<String, CartItem> cart = getCartItemsWithQuantities();

        if (cart.containsKey(itemId)) {
            CartItem cartItem = cart.get(itemId);
            cartItem.setQuantity(cartItem.getQuantity() + 1);
        } else {
            cart.put(itemId, new CartItem(item, 1));
        }

        saveCart(cart);
        notifyCartChange(); // Notify listeners
    }

    public void increaseItemQuantity(Item item) {
        String itemId = item.getId();
        Map<String, CartItem> cart = getCartItemsWithQuantities();

        if (cart.containsKey(itemId)) {
            CartItem cartItem = cart.get(itemId);
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            saveCart(cart);
            notifyCartChange(); // Notify listeners
        }
    }

    public void decreaseItemQuantity(Item item) {
        String itemId = item.getId();
        Map<String, CartItem> cart = getCartItemsWithQuantities();

        if (cart.containsKey(itemId)) {
            CartItem cartItem = cart.get(itemId);
            if (cartItem.getQuantity() > 1) {
                cartItem.setQuantity(cartItem.getQuantity() - 1);
            } else {
                cart.remove(itemId);
            }
            saveCart(cart);
            notifyCartChange(); // Notify listeners
        }
    }

    public void removeItemFromCart(Item item) {
        String itemId = item.getId();
        Map<String, CartItem> cart = getCartItemsWithQuantities();
        if (cart.containsKey(itemId)) {
            cart.remove(itemId);
            saveCart(cart);
            notifyCartChange(); // Notify listeners
        }
    }

    public void clearCart() {
        sharedPreferences.edit().remove("cart_items").apply(); // Clear the cart
        notifyCartChange(); // Notify listeners
    }

    public double getTotalAmount() {
        Map<String, CartItem> cartItems = getCartItemsWithQuantities();
        double totalAmount = 0.0;

        for (CartItem cartItem : cartItems.values()) {
            totalAmount += cartItem.getItem().getPrice() * cartItem.getQuantity();
        }

        return totalAmount; // Return the total amount
    }

    public Map<String, CartItem> getCartItemsWithQuantities() {
        String cartJson = sharedPreferences.getString("cart_items", "");
        if (!cartJson.isEmpty()) {
            Type type = new TypeToken<Map<String, CartItem>>() {}.getType();
            return gson.fromJson(cartJson, type);
        }
        return new HashMap<>();  // Return empty cart if no items found
    }

    private void saveCart(Map<String, CartItem> cart) {
        String cartJson = gson.toJson(cart);
        sharedPreferences.edit().putString("cart_items", cartJson).apply();
    }

    public int getItemQuantity(Item item) {
        String itemId = item.getId();
        Map<String, CartItem> cart = getCartItemsWithQuantities();
        return cart.containsKey(itemId) ? cart.get(itemId).getQuantity() : 0;
    }
}
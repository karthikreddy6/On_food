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
    private static volatile CartManager instance;
    private List<CartChangeListener> listeners = new ArrayList<>();

    public static CartManager getInstance(Context context) {
        if (instance == null) {
            synchronized (CartManager.class) {
                if (instance == null) {
                    instance = new CartManager(context);
                }
            }
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

    public synchronized void addToCart(Item item) {
        String itemId = item.getId();
        Map<String, CartItem> cart = getCartItemsWithQuantities();

        cart.put(itemId, cart.getOrDefault(itemId, new CartItem(item, 0)));
        cart.get(itemId).setQuantity(cart.get(itemId).getQuantity() + 1);

        saveCart(cart);
        notifyCartChange();
    }

    public synchronized void increaseItemQuantity(Item item) {
        String itemId = item.getId();
        Map<String, CartItem> cart = getCartItemsWithQuantities();


        if (cart.containsKey(itemId)) {
            cart.get(itemId).setQuantity(cart.get(itemId).getQuantity() + 1);
            saveCart(cart);
            notifyCartChange();
        }
    }

    public synchronized void decreaseItemQuantity(Item item) {
        String itemId = item.getId();
        Map<String, CartItem> cart = getCartItemsWithQuantities();

        if (cart.containsKey(itemId)) {
            if (cart.get(itemId).getQuantity() > 1) {
                cart.get(itemId).setQuantity(cart.get(itemId).getQuantity() - 1);
            } else {
                cart.remove(itemId);
            }
            saveCart(cart);
            notifyCartChange();
        }
    }

    public synchronized void removeItemFromCart(Item item) {
        String itemId = item.getId();
        Map<String, CartItem> cart = getCartItemsWithQuantities();
        if (cart.containsKey(itemId)) {
            cart.remove(itemId);
            saveCart(cart);
            notifyCartChange();
        }
    }

    public synchronized void clearCart() {
        sharedPreferences.edit().clear().apply();
        notifyCartChange();
    }

    public double getTotalAmount() {
        double totalAmount = 0.0;
        for (CartItem cartItem : getCartItemsWithQuantities().values()) {
            totalAmount += cartItem.getItem().getPrice() * cartItem.getQuantity();
        }
        return totalAmount;
    }

    public Map<String, CartItem> getCartItemsWithQuantities() {
        String cartJson = sharedPreferences.getString("cart_items", "");
        return cartJson.isEmpty() ? new HashMap<>() : gson.fromJson(cartJson, new TypeToken<Map<String, CartItem>>() {}.getType());
    }

    private void saveCart(Map<String, CartItem> cart) {
        if (cart.isEmpty()) {
            sharedPreferences.edit().remove("cart_items").apply();
        } else {
            sharedPreferences.edit().putString("cart_items", gson.toJson(cart)).apply();
        }
    }
    public int getItemQuantity(Item item) {
        String itemId = item.getId();
        Map<String, CartItem> cart = getCartItemsWithQuantities();
        return cart.containsKey(itemId) ? cart.get(itemId).getQuantity() : 0;
    }
    public void updateItemQuantity(Item item, int quantity) {
        String itemId = item.getId();
        Map<String, CartItem> cart = getCartItemsWithQuantities();

        if (cart.containsKey(itemId)) {
            CartItem cartItem = cart.get(itemId);
            if (quantity > 0) { // Only update if quantity is positive
                cartItem.setQuantity(quantity);
            } else {
                cart.remove(itemId); // Remove item if quantity is 0 or less
            }
            saveCart(cart);
        }
    }
}

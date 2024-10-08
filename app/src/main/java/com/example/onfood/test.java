//package com.example.onfood;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//public class CartActivity extends AppCompatActivity {
//    private OrderManager orderManager;
//    private RecyclerView recyclerViewCartItems;
//    private CartManager cartManager;
//    public Button placeorder;
//    public TextView totalAmountTextView;
//    private FirebaseAuth mAuth;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_cart);
//        placeorder = findViewById(R.id.buttonPlaceOrder);
//
//        mAuth = FirebaseAuth.getInstance();
//        FirebaseUser currentUser = mAuth.getCurrentUser();  // Get current logged-in user
//
//        totalAmountTextView = findViewById(R.id.totalAmountTextView);
//        recyclerViewCartItems = findViewById(R.id.recyclerViewCartItems);
//        recyclerViewCartItems.setLayoutManager(new LinearLayoutManager(this));
//
//        cartManager = CartManager.getInstance(this);
//        orderManager = new OrderManager(this);
//
//        placeorder.setOnClickListener(v -> {
//            String userId = currentUser.getUid();  // Get the Firebase User ID (UID)
//
//            // Place the order in Firebase Realtime Database
//            orderManager.placeOrder(userId, orderId -> {
//                // Navigate to OrderConfirmationActivity with the order ID
//                Intent intent = new Intent(CartActivity.this, OrderConfirmationActivity.class);
//                intent.putExtra("ORDER_ID", orderId);
//                startActivity(intent);
//            });
//
//        });
//
//        loadCartItems();
//        displayTotalAmount();
//    }
//
//    private void displayTotalAmount() {
//        double totalAmount = cartManager.getTotalAmount();
//        totalAmountTextView.setText("Total Amount: $" + totalAmount);  // Display total amount
//    }
//
//    private void loadCartItems() {
//        // Fetch cart items from CartManager
//        Map<String, CartItem> cartItemsWithQuantities = cartManager.getCartItemsWithQuantities();
//
//        if (cartItemsWithQuantities.isEmpty()) {
//            Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Convert the map values to a list of CartItem objects
//        List<CartItem> cartItems = new ArrayList<>(cartItemsWithQuantities.values());
//
//        // Set the adapter with the cart items and CartManager
//        CartAdapter cartAdapter = new CartAdapter(cartItems, cartManager);
//        recyclerViewCartItems.setAdapter(cartAdapter);
//    }
//}
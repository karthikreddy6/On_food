package com.example.onfood.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onfood.CartAdapter;
import com.example.onfood.CartItem;
import com.example.onfood.CartManager;
import com.example.onfood.LoadingView;
import com.example.onfood.OrderManager;
import com.example.onfood.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity {
    private OrderManager orderManager;
    private RecyclerView recyclerViewCartItems;
    private CartManager cartManager;
    public Button placeorder;
    public TextView totalAmountTextView;
    private FirebaseAuth mAuth;
    private LoadingView loadingView;
    private ConstraintLayout rootLayout;
private View totalam;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        placeorder = findViewById(R.id.buttonPlaceOrder);


        ImageButton buttonBack = findViewById(R.id.buttonBack);
        ImageButton buttonCart = findViewById(R.id.buttonCart);
        TextView navText =findViewById(R.id.navtext);
        navText.setText("CART");

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();  // Get current logged-in user

        buttonCart.setOnClickListener(v -> startActivity(new Intent(CartActivity.this,OrderHistoryActivity.class)));
        buttonBack.setOnClickListener(v -> onBackPressed());
        buttonBack.setVisibility(View.VISIBLE);
        buttonCart.setVisibility(View.VISIBLE);

        totalAmountTextView = findViewById(R.id.totalAmountTextView);
        recyclerViewCartItems = findViewById(R.id.recyclerViewCartItems);
        recyclerViewCartItems.setLayoutManager(new LinearLayoutManager(this));

        cartManager = CartManager.getInstance(this);
        orderManager = new OrderManager(this);

        placeorder.setOnClickListener(v -> {
            String userId = currentUser.getUid();  // Get the Firebase User ID (UID)
            setloadindscreen();
            rmLoadingView();
            // Place the order in Firebase Realtime Database
            orderManager.placeOrder(userId, orderId -> {
                // Navigate to OrderConfirmationActivity with the order ID
                Intent intent = new Intent(CartActivity.this, OrderConfirmationActivity.class);
                intent.putExtra("source", "cart");
                intent.putExtra("ORDER_ID", orderId);
                cartManager.clearCart();
                startActivity(intent);
                finish();
            });

        });

        loadCartItems();
        displayTotalAmount();
    }

    public void displayTotalAmount() {
        double totalAmount = cartManager.getTotalAmount();
        totalAmountTextView.setText("Total Amount: " + totalAmount);  // Display total amount
    }

    private void loadCartItems() {
        // Fetch cart items from CartManager
        Map<String, CartItem> cartItemsWithQuantities = cartManager.getCartItemsWithQuantities();

        if (cartItemsWithQuantities.isEmpty()) {
            Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert the map values to a list of CartItem objects
        List<CartItem> cartItems = new ArrayList<>(cartItemsWithQuantities.values());

        // Set the adapter with the cart items and CartManager
        CartAdapter cartAdapter = new CartAdapter(cartItems, cartManager, this);
        recyclerViewCartItems.setAdapter(cartAdapter);
    }
    private void rmLoadingView(){
        rootLayout.postDelayed(() -> {
            loadingView.setVisibility(View.GONE); // Hide the loading view
            rootLayout.setVisibility(View.VISIBLE); // Show the main content
            rootLayout.requestLayout(); // Refresh layout
            rootLayout.invalidate(); // Redraw layout
        }, 1500); // Adjust this delay
    }

    private  void setloadindscreen(){
        rootLayout = findViewById(R.id.cart);
        totalam =findViewById(R.id.total);
        totalam.setVisibility(View.GONE);
        loadingView = new LoadingView(this);
        loadingView.setVisibility(View.VISIBLE);
        rootLayout.addView(loadingView);

    }

}
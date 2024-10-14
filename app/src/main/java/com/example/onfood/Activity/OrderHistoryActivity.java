package com.example.onfood.Activity;

import static com.example.onfood.R.id.buttonBack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onfood.Order;
import com.example.onfood.OrderAdapter;
import com.example.onfood.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerViewOrders;
    private DatabaseReference ordersRef;
    private FirebaseAuth mAuth;
    private List<Order> orderList; // Changed to Order object

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        // Include navigation bar
        View navigationBar = getLayoutInflater().inflate(R.layout.navigation_bar, null);
        ((ViewGroup) findViewById(R.id.navigationContainer)).addView(navigationBar);


        ImageButton buttonBack = findViewById(R.id.buttonBack);
        TextView navText = findViewById(R.id.navtext);
        buttonBack.setOnClickListener(v -> onBackPressed());
        buttonBack.setVisibility(View.VISIBLE);
        navText.setText("HISTORY");

        // Initialize RecyclerView
        recyclerViewOrders = findViewById(R.id.recyclerViewOrders);
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
        orderList = new ArrayList<>();
        ordersRef = FirebaseDatabase.getInstance().getReference("Orders");
        mAuth = FirebaseAuth.getInstance();

        fetchOrderHistory();
    }

    private void fetchOrderHistory() {
        String userId = mAuth.getCurrentUser().getUid(); // Get the current user ID

        ordersRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                orderList.clear(); // Clear previous orders
                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                    String orderId = orderSnapshot.getKey();
                    double amount = orderSnapshot.child("amount").getValue(Double.class);
                    String date = orderSnapshot.child("orderDate").getValue(String.class);
                    String time = orderSnapshot.child("orderTime").getValue(String.class);

                    // Create Order object
                    Order order = new Order(orderId, amount, date, time);
                    orderList.add(order);
                }

                if (orderList.isEmpty()) {
                    Toast.makeText(OrderHistoryActivity.this, "No previous orders found.", Toast.LENGTH_SHORT).show();
                } else {
                    OrderAdapter adapter = new OrderAdapter(orderList);
                    recyclerViewOrders.setAdapter(adapter);

                    // Set item click listener to navigate to OrderConfirmationActivity
                    adapter.setOnItemClickListener(position -> {
                        String selectedOrderId = orderList.get(position).getOrderId(); // Get the order ID
                        Intent intent = new Intent(OrderHistoryActivity.this, OrderConfirmationActivity.class);
                        intent.putExtra("ORDER_ID", selectedOrderId); // Pass the order ID
                        startActivity(intent);
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(OrderHistoryActivity.this, "Error fetching order history.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}